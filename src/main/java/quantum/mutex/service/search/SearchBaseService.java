/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import quantum.mutex.util.QueryUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.IndexNameSuffix;

/**
 *
 * @author Florent
 */

public class SearchBaseService {

    private static final Logger LOG = Logger.getLogger(SearchBaseService.class.getName());
    
    @Inject ApiClientUtils acu;
    @Inject QueryUtils queryUtils;
    
    protected Result<SearchResponse> search(SearchRequest sr){
        try {
            return Result.success(acu.getHighLevelPostClient()
                    .search(sr, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            
            return Result.failure(ex);
        }
    }
    
    protected Result<SearchRequest> getSearchRequest(List<Group> groups,SearchSourceBuilder sb){
        String[] indices = groups.stream()
                .map(g -> queryUtils.indexName(g,IndexNameSuffix.VIRTUAL_PAGE.value()))
                .toArray(String[]::new);
//        Arrays.stream(indices)
//                .forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        var sr = new SearchRequest(indices, sb);
       
        return Result.of(sr);
    }
    
     protected Result<SearchRequest> getCompleteRequest(List<Group> groups,SearchSourceBuilder sb){
        String[] indices = groups.stream()
                .map(g -> queryUtils.indexName(g,IndexNameSuffix.TERM_COMPLETION.value()))
                .toArray(String[]::new);
        Arrays.stream(indices)
                .forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        var sr = new SearchRequest(indices, sb);
       
        return Result.of(sr);
    }
    
    protected Result<SearchSourceBuilder> getSearchSourceBuilder(QueryBuilder queryBuilder){
       var searchSourceBuilder = new SearchSourceBuilder();
       return Result.of(searchSourceBuilder.query(queryBuilder));
    }
    
    protected Result<SearchSourceBuilder> getSearchSourceBuilder(SuggestBuilder suggestBuilder){
       var searchSourceBuilder = new SearchSourceBuilder();
       return Result.of(searchSourceBuilder.suggest(suggestBuilder));
    }
       
    protected Result<SearchSourceBuilder> provideHighlightBuilder(SearchSourceBuilder ssb,HighlightBuilder hb){
       ssb.highlighter(hb);
       return Result.of(ssb);
    }
    
    protected List<SearchHit> getSearchHits(SearchResponse sr){
       SearchHits hits = sr.getHits();
       return Arrays.stream(hits.getHits()).collect(Collectors.toList());
    }
    
}
