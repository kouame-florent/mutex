/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.DateRangeCriteria;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.dto.OwnerCreteria;
import quantum.mutex.domain.dto.SizeRangeCriteria;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.IndexNameSuffix;
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
    
//    public List<SearchHit> searchByDateRange(LocalDateTime from, LocalDateTime to,List<Group> groups){
//        return getSearchSourceBuilder(from, to)
//                .flatMap(ssb -> getSearchRequest(ssb, groups))
//                .flatMap(srq -> doSearch(srq))
//                .map(srp -> getHits(srp))
//                .getOrElse(() -> Collections.EMPTY_LIST);
//    }
//    
//    public List<SearchHit> searchBySizeRange(long from,long to,List<Group> groups){
//        return getSearchSourceBuilder(from, to)
//                .flatMap(ssb -> getSearchRequest(ssb, groups))
//                .flatMap(this::doSearch)
//                .map(this::getHits)
//                .getOrElse(() -> Collections.EMPTY_LIST);
//    }
//    
//    public List<SearchHit> searchByOwners(List<String> owners,List<Group> groups){
//        return getSearchSourceBuilder(owners)
//                .flatMap(ssb -> getSearchRequest(ssb, groups))
//                .flatMap(this::doSearch)
//                .map(this::getHits)
//                .getOrElse(() -> Collections.EMPTY_LIST);
//    }
//    
    public List<SearchHit> search(OwnerCreteria oc, DateRangeCriteria dc,
            SizeRangeCriteria sc,List<Group> groups){
        return createSourceBuilder(oc,dc,sc)
                .flatMap(ssb -> getSearchRequest(ssb, groups))
                .flatMap(this::doSearch)
                .map(this::getHits)
                .getOrElse(() -> Collections.EMPTY_LIST);
    }
    
//    private Result<SearchSourceBuilder> getSearchSourceBuilder(List<String> owners){
//        QueryBuilder queryBuilder = 
//                searchOwnersQueryBuilder(MetadataProperty.FILE_OWNER, owners);
//        return coreSearchService.getSearchSourceBuilder(queryBuilder);
//    }
//    
//    private Result<SearchSourceBuilder> getSearchSourceBuilder(Long from, Long to){
//       QueryBuilder queryBuilder = 
//                searchSizeRangeQueryBuilder(MetadataProperty.FILE_SIZE,from, to);
//        return coreSearchService.getSearchSourceBuilder(queryBuilder);
//    }
//    
//    private Result<SearchSourceBuilder> getSearchSourceBuilder(LocalDateTime from, LocalDateTime to){
//        QueryBuilder queryBuilder = 
//                searchDateRangeQueryBuilder(MetadataProperty.FILE_CREATED,from, to);
//        return coreSearchService.getSearchSourceBuilder(queryBuilder);
//    }
    
    private Result<SearchSourceBuilder> createSourceBuilder(OwnerCreteria oc,
            DateRangeCriteria dc,SizeRangeCriteria sc){
        List<Result<QueryBuilder>> rQueryBuilders = 
                List.of(searchOwnersQueryBuilder(MetadataProperty.FILE_OWNER, oc),
                searchDateRangeQueryBuilder(MetadataProperty.FILE_CREATED, dc), 
                searchSizeRangeQueryBuilder(MetadataProperty.FILE_SIZE, sc));
        
        List<QueryBuilder> builders = rQueryBuilders.stream().filter(Result::isSuccess)
                .map(Result::successValue).collect(Collectors.toList());
        return coreSearchService.getSearchSourceBuilder(aggregateBuilder(builders));
    }
    
    private List<SearchHit> getHits(SearchResponse searchResponse){
      return  coreSearchService.getSearchHits(searchResponse);
    }
    
    private Result<QueryBuilder> searchOwnersQueryBuilder(MetadataProperty property,OwnerCreteria oc){
        return oc.isValid() ? Result.of(QueryBuilders.termsQuery(property.value(), oc.owners())) : 
                Result.empty();
    }
     
    private Result<QueryBuilder> searchDateRangeQueryBuilder(MetadataProperty property, 
            DateRangeCriteria dc){
        if(dc.isValid()){
            Result<LocalDateTime>  oStart = dc.startDate();
            Result<LocalDateTime> oEnd = dc.endDate();
            var query = QueryBuilders.rangeQuery(property.value());
            return oStart.map(s -> query.from(s))
                    .flatMap(q -> oEnd.map(e -> q.to(e)));
        }
        return Result.empty();
    }
    
    private Result<QueryBuilder> searchSizeRangeQueryBuilder(MetadataProperty property,
            SizeRangeCriteria sc){
        if(sc.isValid()){
            var query = QueryBuilders.rangeQuery(property.value());
            query.from(sc.startSize(),true);
            query.to(sc.endSize(), true);
            
            return Result.of(query);
        }
        return Result.empty(); 
    }
    
    private QueryBuilder aggregateBuilder(List<QueryBuilder> builders){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builders.stream().forEach(qb -> boolQueryBuilder.must(qb));
        LOG.log(Level.INFO, "--> RANGE SIZE QUERY: {0}", boolQueryBuilder.toString());
        return boolQueryBuilder;
    }
    
     private Result<SearchRequest> getSearchRequest(SearchSourceBuilder ssb,List<Group> groups){
        Result<SearchRequest> rSearchRequest = 
                coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.METADATA);
        rSearchRequest.forEachOrException(sr -> elApiUtil.logJson(sr))
                .forEach(e -> e.printStackTrace());
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
}
