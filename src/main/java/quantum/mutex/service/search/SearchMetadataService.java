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
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.ContentCriteria;
import quantum.mutex.domain.dto.DateRangeCriteria;
import quantum.mutex.domain.dto.MetaFragment;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.dto.OwnerCreteria;
import quantum.mutex.domain.dto.SearchCriteria;
import quantum.mutex.domain.dto.SizeRangeCriteria;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.util.AggregationProperty;
import quantum.mutex.util.CriteriaName;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.EnvironmentUtils;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.MetaFragmentProperty;
import quantum.mutex.util.MetadataProperty;

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
           
    public Set<MetaFragment> search(List<Group> selectedGroups,Map<CriteriaName,SearchCriteria> criterias){
        return search_(criterias, currentGroups(selectedGroups));
   }
    
    private List<Group> currentGroups(List<Group> selectedGroups){
        return selectedGroups.isEmpty() ? envUtils.getUser()
                    .map(u -> userGroupService.getAllGroups(u))
                    .getOrElse(() -> Collections.EMPTY_LIST)
                : selectedGroups;
    }
    
    private Set<MetaFragment> search_(Map<CriteriaName,SearchCriteria> criterias,List<Group> groups){
        Result<SearchRequest> rSearchRequest = createSourceBuilder(criterias)
            .flatMap(ssb -> scs.addSizeLimit(ssb, 0))
            .flatMap(ssb -> makeTermsAggregationBuilder().flatMap(tab -> scs.provideAggregate(ssb, tab)))
            .flatMap(ssb -> getSearchRequest(ssb, groups));
        
        rSearchRequest.forEachOrException(elApiUtil::logJson)
                .forEach(e -> LOG.log(Level.SEVERE, "ERROR: {0}", e));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> scs.search(sr));
               
        Set<MetaFragment> fragments = rResponse.map(r -> extractFragments(r))
                .getOrElse(() -> Collections.EMPTY_SET);
        
        LOG.log(Level.INFO, "-->< FRAGMENTS SIZE: {0}", fragments.size());
        
        return fragments;
    
    }
    
//    private void processSearchStack(List<Group> groups){
//        searchCriteria.clear();
//        addContentCriteria(searchText);
//        fragments = searchMetadataService.search(searchCriteria,groups);
//    }
  
    private Result<SearchSourceBuilder> createSourceBuilder(Map<CriteriaName,SearchCriteria> criterias){
        List<Result<QueryBuilder>> rQueryBuilders = 
            List.of(searchMatchQueryBuilder(MetadataProperty.CONTENT, getContentCriterion(criterias)),
                searchOwnersQueryBuilder(MetadataProperty.FILE_OWNER, getOwnerCriterion(criterias)),
                searchDateRangeQueryBuilder(MetadataProperty.FILE_CREATED, getDateCriterion(criterias)), 
                searchSizeRangeQueryBuilder(MetadataProperty.FILE_SIZE, getSizeCriterion(criterias)));
        
        List<QueryBuilder> builders = rQueryBuilders.stream().filter(Result::isSuccess)
                .map(Result::successValue).collect(toList());
        return scs.getSearchSourceBuilder(composeBuilder(builders));
    }
    
    private ContentCriteria getContentCriterion( Map<CriteriaName,SearchCriteria> criteria){
        return (ContentCriteria)criteria.getOrDefault(CriteriaName.CONTENT, ContentCriteria.getDefault());
    }
    
    private SizeRangeCriteria getSizeCriterion( Map<CriteriaName,SearchCriteria> criteria){
        return (SizeRangeCriteria)criteria.getOrDefault(CriteriaName.SIZE_RANGE, SizeRangeCriteria.getDefault());
    }
    
    private DateRangeCriteria getDateCriterion( Map<CriteriaName,SearchCriteria> criteria){
        return (DateRangeCriteria)criteria.getOrDefault(CriteriaName.DATE_RANGE, DateRangeCriteria.getDefault());
    }
    
    private OwnerCreteria getOwnerCriterion( Map<CriteriaName,SearchCriteria> criteria){
        return (OwnerCreteria)criteria.getOrDefault(CriteriaName.OWNER, OwnerCreteria.getDefault());
   }
    
    private Result<QueryBuilder> searchMatchQueryBuilder(MetadataProperty property,ContentCriteria cc){
        return cc.isValid() ? Result.of(QueryBuilders.matchQuery(property.value(), cc.searchText())) : 
                Result.empty();
    }
    
    private Result<QueryBuilder> searchOwnersQueryBuilder(MetadataProperty property,OwnerCreteria oc){
        return oc.isValid() ? Result.of(QueryBuilders.termsQuery(property.value(), oc.owners())) : 
                Result.empty();
    }
     
    private Result<QueryBuilder> searchDateRangeQueryBuilder(MetadataProperty property, 
            DateRangeCriteria dc){
        if(dc.isValid()){
            long start = dc.startDate();
            long end = dc.endDate();
            var query = QueryBuilders.rangeQuery(property.value());
            query.from(start);
            query.to(end);
            return Result.of(query);       
        }
        return Result.empty();
    }
    
    private Result<QueryBuilder> searchSizeRangeQueryBuilder(MetadataProperty property,
            SizeRangeCriteria sc){
        return Result.of(sc).filter(s -> s.isValid()).flatMap(s -> queryWithSizeRange(property, s));
    }
    
    private Result<QueryBuilder> queryWithSizeRange(MetadataProperty property,SizeRangeCriteria sc){
        var query = QueryBuilders.rangeQuery(property.value());
        query.from(sc.minSize(),true);
        query.to(sc.maxSize(), true);
        return Result.of(query);
    }
    
    private QueryBuilder composeBuilder(List<QueryBuilder> builders){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builders.stream().forEach(qb -> boolQueryBuilder.must(qb));
        LOG.log(Level.INFO, "--> AGGREGATE QUERY: {0}", boolQueryBuilder.toString());
        return boolQueryBuilder;
    }
    
    private Result<SearchRequest> getSearchRequest(SearchSourceBuilder ssb,List<Group> groups){
        Result<SearchRequest> rSearchRequest = 
                scs.getSearchRequest(groups,ssb,IndexNameSuffix.METADATA);
        rSearchRequest.forEachOrException(sr -> elApiUtil.logJson(sr))
                .forEach(e -> LOG.log(Level.SEVERE,"EXECPTION: {0}", e));
        return rSearchRequest;
    }
    
    public Result<Metadata> toMetadata(SearchHit hit){
        Metadata m = new Metadata();
        m.setContent((String)hit.getSourceAsMap().get(MetadataProperty.CONTENT.value()));
        m.setFileName((String)hit.getSourceAsMap().get(MetadataProperty.FILE_NAME.value()));
        return Result.of(m);
    }
    
    public Set<MetaFragment> extractFragments(SearchResponse searchResponse){
        List<SearchHit> hits = scs.getTermsAggregations(searchResponse,
                AggregationProperty.META_TERMS_VALUE.value())
            .map(t -> scs.getBuckets(t))
            .map(bs -> scs.getTopHits(bs,AggregationProperty.META_TOP_HITS_VALUE.value()))
            .map(ths -> scs.getSearchHits(ths))
            .getOrElse(() -> Collections.EMPTY_LIST);
      
        LOG.log(Level.INFO,"--<> HITS SIZE: {0}" ,hits.size());
        
        return toFragments(hits);
    }
    
    private Set<MetaFragment> toFragments(List<SearchHit> hits){
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
    
   private String getHighlighted( SearchHit hit){
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlight = highlightFields.get(MetaFragmentProperty.CONTENT.value()); 
        return Arrays.stream(highlight.getFragments()).map(t -> t.string())
                .collect(Collectors.joining("..."));
    }
    
    private Result<AggregationBuilder> makeTermsAggregationBuilder(){
        HighlightBuilder hlb = scs.getHighlightBuilder(MetadataProperty.CONTENT.value())
                .getOrElse(() -> new HighlightBuilder() );
        AggregationBuilder aggregation = AggregationBuilders
            .terms(AggregationProperty.META_TERMS_VALUE.value())
                .field(AggregationProperty.META_FIELD_VALUE.value())
            .subAggregation(
                AggregationBuilders.topHits(AggregationProperty.META_TOP_HITS_VALUE.value())
                   .highlighter(hlb)
                   .size(2)
                   .from(0)
            );
        return Result.of(aggregation);
   }
}
