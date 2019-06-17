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
import javax.validation.constraints.NotNull;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
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
import quantum.mutex.util.Constants;
import quantum.mutex.util.CriteriaName;
import quantum.mutex.util.ElApiUtil;
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
        
    @Inject SearchCoreService coreSearchService;
    @Inject ElApiUtil elApiUtil;
           
    public Set<MetaFragment> search(Map<CriteriaName,SearchCriteria> criterias,List<Group> groups){
        return search_(criterias, groups);
    }
    
    public Set<MetaFragment> search_(Map<CriteriaName,SearchCriteria> criterias,List<Group> groups){
        List<SearchHit> hits = createSourceBuilder(criterias)
                .flatMap(ssb -> highlightBuilder().flatMap(hlb -> coreSearchService.provideHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> getSearchRequest(ssb, groups))
                .flatMap(this::doSearch)
                .map(this::getHits)
                .getOrElse(() -> Collections.EMPTY_LIST);
        
       return hits.stream().map(this::toMutexFragment).collect(toSet());
                
    }
  
    private Result<SearchSourceBuilder> createSourceBuilder(Map<CriteriaName,SearchCriteria> criterias){
        List<Result<QueryBuilder>> rQueryBuilders = 
            List.of(searchMatchQueryBuilder(MetadataProperty.CONTENT, getContentCriterion(criterias)),
                searchOwnersQueryBuilder(MetadataProperty.FILE_OWNER, getOwnerCriterion(criterias)),
                searchDateRangeQueryBuilder(MetadataProperty.FILE_CREATED, getDateCriterion(criterias)), 
                searchSizeRangeQueryBuilder(MetadataProperty.FILE_SIZE, getSizeCriterion(criterias)));
        
        List<QueryBuilder> builders = rQueryBuilders.stream().filter(Result::isSuccess)
                .map(Result::successValue).collect(toList());
        return coreSearchService.getSearchSourceBuilder(composeBuilder(builders));
    }
    
    private ContentCriteria getContentCriterion(@NotNull Map<CriteriaName,SearchCriteria> criteria){
        return (ContentCriteria)criteria.getOrDefault(CriteriaName.CONTENT, ContentCriteria.getDefault());
    }
    
    private SizeRangeCriteria getSizeCriterion(@NotNull Map<CriteriaName,SearchCriteria> criteria){
        return (SizeRangeCriteria)criteria.getOrDefault(CriteriaName.SIZE_RANGE, SizeRangeCriteria.getDefault());
    }
    
    private DateRangeCriteria getDateCriterion(@NotNull Map<CriteriaName,SearchCriteria> criteria){
        return (DateRangeCriteria)criteria.getOrDefault(CriteriaName.DATE_RANGE, DateRangeCriteria.getDefault());
    }
    
    private OwnerCreteria getOwnerCriterion(@NotNull Map<CriteriaName,SearchCriteria> criteria){
        return (OwnerCreteria)criteria.getOrDefault(CriteriaName.OWNER, OwnerCreteria.getDefault());
   }
    
    private List<SearchHit> getHits(SearchResponse searchResponse){
      return  coreSearchService.getSearchHits(searchResponse);
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
                coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.METADATA);
        rSearchRequest.forEachOrException(sr -> elApiUtil.logJson(sr))
                .forEach(e -> LOG.log(Level.SEVERE,"EXECPTION: {0}", e));
        return rSearchRequest;
    }
    
    private Result<SearchResponse> doSearch(SearchRequest searchRequest){
        return coreSearchService.search(searchRequest);
    }
    
    public Result<Metadata> toMetadata(SearchHit hit){
        Metadata m = new Metadata();
        m.setContent((String)hit.getSourceAsMap().get(MetadataProperty.CONTENT.value()));
        m.setFileName((String)hit.getSourceAsMap().get(MetadataProperty.FILE_NAME.value()));
        return Result.of(m);
    }
    
    private MetaFragment toMutexFragment(@NotNull SearchHit hit){
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
    
    private Result<HighlightBuilder> highlightBuilder(){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent =
               new HighlightBuilder.Field(MetaFragmentProperty.CONTENT.value());
        highlightBuilder.field(highlightContent.numOfFragments(Constants.HIGHLIGHT_NUMBER_OF_FRAGMENTS)
                                .preTags(Constants.HIGHLIGHT_PRE_TAG)
                                .postTags(Constants.HIGHLIGHT_POST_TAG));
        return Result.of(highlightBuilder);
   }
    
   private String getHighlighted(@NotNull SearchHit hit){
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlight = highlightFields.get(MetaFragmentProperty.CONTENT.value()); 
        return Arrays.stream(highlight.getFragments()).map(t -> t.string())
                .collect(Collectors.joining("..."));
    }
}
