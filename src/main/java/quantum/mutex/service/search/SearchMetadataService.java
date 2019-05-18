/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.Metadata;
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
    
    public List<SearchHit> searchForDateRange(LocalDateTime from, LocalDateTime to,List<Group> groups){
        return getSearchSourceBuilder(from, to)
                .flatMap(ssb -> getSearchRequest(ssb, groups))
                .flatMap(srq -> doSearch(srq))
                .map(srp -> getHits(srp))
                .getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    private Result<SearchSourceBuilder> getSearchSourceBuilder(LocalDateTime from, LocalDateTime to){
        Result<QueryBuilder> rQueryBuilder = 
                searchDateRangeQueryBuilder(MetadataProperty.FILE_CREATED,from, to);
        return rQueryBuilder.flatMap(qb -> coreSearchService.getSearchSourceBuilder(qb) );
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
    
    private List<SearchHit> getHits(SearchResponse searchResponse){
      return  coreSearchService.getSearchHits(searchResponse);
    }
     
    private Result<QueryBuilder> searchDateRangeQueryBuilder(MetadataProperty property,
            LocalDateTime from, LocalDateTime to){
        var query = QueryBuilders.rangeQuery(property.value());
        query.from(from,true);
        query.to(to, true);
        LOG.log(Level.INFO, "--> RANGE QUERY: {0}", query.toString());
        return Result.of(query);
    }
    
    public Result<Metadata> toMetadata(SearchHit hit){
        Metadata m = new Metadata();
        m.setContent((String)hit.getSourceAsMap().get(MetadataProperty.CONTENT.value()));
        m.setFileName((String)hit.getSourceAsMap().get(MetadataProperty.FILE_NAME.value()));
        return Result.of(m);
    }
}
