/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import quantum.mutex.util.RestClientUtil;
import quantum.mutex.util.QueryUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.AggregationProperty;
import quantum.mutex.util.IndexNameSuffix;

/**
 *
 * @author Florent
 */
@Stateless
public class SearchCoreService {

    private static final Logger LOG = Logger.getLogger(SearchCoreService.class.getName());
    
    @Inject RestClientUtil restClientUtil;
    @Inject QueryUtils queryUtils;
   
    public Result<SearchResponse> search(SearchRequest sr){
        try {
            return Result.success(restClientUtil.getElClient()
                    .search(sr, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            
            return Result.failure(ex);
        }
    }
    
    public  Result<SearchRequest> getSearchRequest(List<Group> groups,SearchSourceBuilder sb,IndexNameSuffix suffix){
        List<String> lst = groups.stream()
                .map(g -> queryUtils.indexName(g,suffix.value()))
                .filter(name -> name.isSuccess())
                .map(name -> name.successValue())
                .collect(Collectors.toList());
        
        lst.forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        String[] indices = lst.stream().toArray(String[]::new);
        SearchRequest request = new SearchRequest(indices, sb);
        Arrays.stream(request.indices())
                .forEach(i -> LOG.log(Level.INFO, "--|| INDEX IN ARRAYS: {0}",i));
        return Result.of(request);
    }
    
     public  Result<SearchRequest> getTermCompleteRequest(List<Group> groups,SearchSourceBuilder sb){
        String[] indices = groups.stream()
                .map(g -> queryUtils.indexName(g,IndexNameSuffix.TERM_COMPLETION.value()))
                .filter(name -> name.isSuccess())
                .map(name -> name.successValue())
                .toArray(String[]::new );
        Arrays.stream(indices)
                .forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        var sr = new SearchRequest(indices, sb);
       
        return Result.of(sr);
    }
    
     public  Result<SearchSourceBuilder> getSearchSourceBuilder(QueryBuilder queryBuilder){
       var searchSourceBuilder = new SearchSourceBuilder();
       return Result.of(searchSourceBuilder.query(queryBuilder));
    }
     
    public Result<SearchSourceBuilder> addSizeLimit(SearchSourceBuilder ssb,int size){
       return Result.of(ssb.size(size));
    }
    
    public  Result<SearchSourceBuilder> getSearchSourceBuilder(SuggestBuilder suggestBuilder){
       var searchSourceBuilder = new SearchSourceBuilder();
       return Result.of(searchSourceBuilder.suggest(suggestBuilder));
    }
       
    public  Result<SearchSourceBuilder> provideHighlightBuilder(SearchSourceBuilder ssb,HighlightBuilder hb){
       return Result.of(ssb.highlighter(hb));
    }
         
    public Result<SearchSourceBuilder> provideAggregate(SearchSourceBuilder ssb,AggregationBuilder aggb){
        return Result.of(ssb.aggregation(aggb));
    }
    
    public List<SearchHit> getSearchHits(SearchResponse sr){
       SearchHits hits = sr.getHits();
       return Arrays.stream(hits.getHits()).collect(Collectors.toList());
    }
    
    public List<SearchHit> searchHits(TopHits topHits){
       return Arrays.stream(topHits.getHits().getHits())
               .collect(Collectors.toList());
    }
    
    public List<SearchHit> getSearchHits(List<TopHits> topHits){
       return topHits.stream().map(this::searchHits)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
        
    public Result<Terms> getTermsAggregations(SearchResponse sr){
      Terms terms =  sr.getAggregations().get(AggregationProperty.TERMS_VALUE.value());
      LOG.log(Level.INFO, "-> RETURN TYPE: {0}", terms);
      return Result.of(terms);

    }
    
    public List<Terms.Bucket> getBuckets(Terms terms){
        return (List<Terms.Bucket>) terms.getBuckets();
    }
    
       
    public List<TopHits> getTopHits(List<Terms.Bucket> buckets){
        return buckets.stream().map(topHits).collect(toList());
    }
    
    private final Function<Terms.Bucket,TopHits> topHits
            = (Terms.Bucket b) -> b.getAggregations().get(AggregationProperty.TOP_HITS_VALUE.value());
    
    public List<SearchHit> getTopSearchHits(List<TopHits> topHits){
        return topHits.stream().map(topSearchHits).flatMap(List::stream)
                .collect(toList());
                
    }
    
    private final Function<TopHits,List<SearchHit>> topSearchHits
            = (TopHits th) -> Arrays.stream(th.getHits().getHits()).collect(toList());
    
}
