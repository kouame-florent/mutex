/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import quantum.mutex.domain.type.criterion.DateRangeCriterion;
import quantum.mutex.domain.type.MetaFragment;
import quantum.mutex.domain.type.Metadata;
import quantum.mutex.domain.type.criterion.OwnerCreterion;
import quantum.mutex.domain.type.criterion.SizeRangeCriterion;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.util.AggregationProperty;
import quantum.mutex.util.CriteriaType;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.EnvironmentUtils;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.MetaFragmentProperty;
import quantum.mutex.util.MetadataProperty;
import quantum.mutex.domain.type.criterion.TextCriterion;
import quantum.mutex.util.Constants;


/**
 *
 * @author Florent
 */
@RequestScoped
public class SearchMetadataService {

    private static final Logger LOG = Logger.getLogger(SearchMetadataService.class.getName());
        
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils envUtils;
    @Inject SearchCoreService scs;
    @Inject ElApiUtil elApiUtil;
           
    public Set<MetaFragment> search(List<Group> selectedGroups,Map<CriteriaType,Object> criterias){
        return search_(criterias, currentGroups(selectedGroups));
   }
   
    private Set<MetaFragment> search_(Map<CriteriaType,Object> criteria,List<Group> groups){
        Optional<SearchRequest> rSearchRequest = createSourceBuilder(criteria)
            .flatMap(ssb -> scs.addSizeLimit(ssb, 0))
            .flatMap(ssb -> composeTermsAggregation().flatMap(tab -> scs.addAggregate(ssb, tab)))
            .flatMap(ssb -> makeSearchRequest(ssb, groups));
        
        rSearchRequest.ifPresent(elApiUtil::logJson);
                
        
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> scs.search(sr));
               
        Set<MetaFragment> fragments = rResponse.map(r -> extractFragments(r))
                .orElseGet(() -> Collections.EMPTY_SET);
        
        LOG.log(Level.INFO, "-->< FRAGMENTS SIZE: {0}", fragments.size());
        
        return fragments;
    
    }
    
    private Optional<SearchSourceBuilder> createSourceBuilder(Map<CriteriaType,Object> criterias){
        List<Optional<QueryBuilder>> rQueryBuilders = 
            List.of(searchMatchQueryBuilder(MetadataProperty.CONTENT, makeTextCriterion(criterias)),
                searchOwnersQueryBuilder(MetadataProperty.FILE_OWNER, makeOwnerCriterion(criterias)),
                searchDateRangeQueryBuilder(MetadataProperty.FILE_CREATED, makeDateCriterion(criterias)), 
                searchSizeRangeQueryBuilder(MetadataProperty.FILE_SIZE, makeSizeCriterion(criterias)));
        
        List<QueryBuilder> builders = rQueryBuilders.stream().filter(Optional::isPresent)
                .map(Optional::get).collect(toList());
        return scs.makeSearchSourceBuilder(composeBuilder(builders));
    }
    
    private QueryBuilder composeBuilder(List<QueryBuilder> builders){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builders.stream().forEach(qb -> boolQueryBuilder.must(qb));
        LOG.log(Level.INFO, "--> AGGREGATE QUERY: {0}", boolQueryBuilder.toString());
        return boolQueryBuilder;
    }
        
    private List<Group> currentGroups(List<Group> selectedGroups){
        return selectedGroups.isEmpty() ? envUtils.getUser()
                    .map(u -> userGroupService.getAllGroups(u))
                    .orElseGet(() -> Collections.EMPTY_LIST)
                : selectedGroups;
    }
   
    private Optional<TextCriterion> makeTextCriterion(Map<CriteriaType,Object> criteria){
       return (Optional<TextCriterion>)criteria
               .getOrDefault(CriteriaType.CONTENT, Optional.empty());
    }
    
    private Optional<SizeRangeCriterion> makeSizeCriterion(Map<CriteriaType,Object> criteria){
        return (Optional<SizeRangeCriterion>)criteria
                .getOrDefault(CriteriaType.SIZE_RANGE, Optional.empty());
    }
     
    private Optional<DateRangeCriterion> makeDateCriterion(Map<CriteriaType,Object> criteria){
        return (Optional<DateRangeCriterion>)criteria
                .getOrDefault(CriteriaType.DATE_RANGE, Optional.empty());
    }
    
    private Optional<OwnerCreterion> makeOwnerCriterion(Map<CriteriaType,Object> criteria){
        return (Optional<OwnerCreterion>)criteria
                .getOrDefault(CriteriaType.OWNER, Optional.empty());
   }
    
    private Optional<QueryBuilder> searchMatchQueryBuilder(MetadataProperty property,Optional<TextCriterion> cc){
        return cc.isPresent()? 
                cc.map(c -> QueryBuilders.matchQuery(property.value(), c.searchText())) 
                : Optional.of(QueryBuilders.regexpQuery(property.value(), Constants.META_DEFAULT_SEARCH_TEXT));
   }
    
    private Optional<QueryBuilder> searchOwnersQueryBuilder(MetadataProperty property,Optional<OwnerCreterion> oc){
        return oc.isPresent()? oc.map(c -> QueryBuilders.termsQuery(property.value(), c.owners()))
                : Optional.empty();
    }
     
    private Optional<QueryBuilder> searchDateRangeQueryBuilder(MetadataProperty property, 
            Optional<DateRangeCriterion> dc){
        
        if(dc.isPresent()){
           var start = dc.map(c -> c.startDate());
           var end = dc.map(c -> c.endDate());
           var query = QueryBuilders.rangeQuery(property.value());
           return start.map(s -> query.from(s))
                   .flatMap(q -> end.map(e -> q.to(e)));
        }else{
           return Optional.empty();
        }
   }
    
    private Optional<QueryBuilder> searchSizeRangeQueryBuilder(MetadataProperty property,
            Optional<SizeRangeCriterion> sc){
        if(sc.isPresent()){
            var min = sc.map(c -> c.minSize());
            var max = sc.map(c -> c.maxSize());
            var query = QueryBuilders.rangeQuery(property.value());
            return min.map(s -> query.from(s))
                   .flatMap(q -> max.map(e -> q.to(e)));
        }else{
           return Optional.empty();
        }
   }
   
  
    
    private Optional<SearchRequest> makeSearchRequest(SearchSourceBuilder ssb,List<Group> groups){
        Optional<SearchRequest> rSearchRequest = 
                scs.getSearchRequest(groups,ssb,IndexNameSuffix.METADATA);
        rSearchRequest.ifPresentOrElse(sr -> elApiUtil.logJson(sr),
                () -> LOG.log(Level.SEVERE,"EXECPTION WHEN MAKING REQUEST"));
        return rSearchRequest;
    }
    
    public Optional<Metadata> toMetadata(SearchHit hit){
        Metadata m = new Metadata();
        m.setContent((String)hit.getSourceAsMap().get(MetadataProperty.CONTENT.value()));
        m.setFileName((String)hit.getSourceAsMap().get(MetadataProperty.FILE_NAME.value()));
        return Optional.of(m);
    }
    
    public Set<MetaFragment> extractFragments(SearchResponse searchResponse){
        List<SearchHit> hits = scs.getTermsAggregations(searchResponse,
                AggregationProperty.META_TERMS_VALUE.value())
            .map(t -> scs.getBuckets(t))
            .map(bs -> scs.getTopHits(bs,AggregationProperty.META_TOP_HITS_VALUE.value()))
            .map(ths -> scs.getSearchHits(ths))
            .orElseGet(() -> Collections.EMPTY_LIST);
      
        LOG.log(Level.INFO,"--<> HITS SIZE: {0}" ,hits.size());
        
        return toFragments(hits);
    }
    
    private Set<MetaFragment> toFragments(List<SearchHit> hits){
        LOG.log(Level.INFO,"--<> BEFORE CONVERT HITS SIZE: {0}" ,hits.size());
        return hits.stream().map(h -> fragment(h))
                .collect(Collectors.toSet());
    }
    
    private MetaFragment fragment(SearchHit hit){
        MetaFragment f = new MetaFragment();
       
        f.setContent(getHighlighted(hit));
        f.setFileOwner((String)hit.getSourceAsMap().get(MetaFragmentProperty.FILE_OWNER.value()));
        f.setFileGroup((String)hit.getSourceAsMap().get(MetaFragmentProperty.FILE_GROUP.value()));
        f.setFileCreated(toLocalDateTime(hit));
        f.setFileMimeType((String)hit.getSourceAsMap().get(MetaFragmentProperty.FILE_MIME_TYPE.value()));
        f.setFileName((String)hit.getSourceAsMap().get(MetaFragmentProperty.FILE_NAME.value()));
        f.setInodeUUID((String)hit.getSourceAsMap().get(MetaFragmentProperty.INODE_UUID.value()));
        return f;
    }
     
    private LocalDateTime toLocalDateTime(SearchHit hit){
        String epochSecondStr = (String)hit.getSourceAsMap().get(MetaFragmentProperty.FILE_CREATED.value());
        try{
            Long  epochSecond = Long.valueOf(epochSecondStr);
            return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
        }catch(NumberFormatException ex){
            LOG.log(Level.SEVERE, "NumberFormatException: ", ex);
            return LocalDateTime.parse("00-00-00");
        }
    }
    
    private String getHighlighted(SearchHit hit){
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        LOG.log(Level.INFO, "HIGHLIGHT FIELD SIZE:{0} ", highlightFields.size());
        HighlightField highlight = highlightFields.get(MetaFragmentProperty.CONTENT.value()); 
        return Arrays.stream(highlight.getFragments()).map(t -> t.string())
                .collect(Collectors.joining("..."));
    }
    
    private Optional<AggregationBuilder> composeTermsAggregation(){
        return scs.makeHighlightBuilder(MetadataProperty.CONTENT.value())
                .map(this::makeTopHitsAggBuilder)
                .map(this::makeTermsAggregationBuilder);
    }
    
    private TopHitsAggregationBuilder makeTopHitsAggBuilder(HighlightBuilder hlb){
        return AggregationBuilders.topHits(AggregationProperty.META_TOP_HITS_VALUE.value())
                   .highlighter(hlb)
                   .size(Constants.TOP_HITS_PER_FILE)
                   .from(0);
    }
    
    private AggregationBuilder makeTermsAggregationBuilder(TopHitsAggregationBuilder thab){
        return AggregationBuilders
            .terms(AggregationProperty.META_TERMS_VALUE.value())
                .field(AggregationProperty.META_FIELD_VALUE.value())
            .subAggregation(thab);
   }
}
