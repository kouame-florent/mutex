/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;

import io.mutex.index.valueobject.RestClientUtil;
import io.mutex.index.valueobject.QueryUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
import io.mutex.user.entity.Group;
import io.mutex.index.valueobject.Constants;
import io.mutex.index.valueobject.IndexNameSuffix;


/**
 *
 * @author Florent
 */
@Stateless
public class SearchCoreService {

    private static final Logger LOG = Logger.getLogger(SearchCoreService.class.getName());
    
    @Inject RestClientUtil restClientUtil;
    @Inject QueryUtils queryUtils;
   
    public Optional<SearchResponse> search(SearchRequest sr){
        try {
            return Optional.ofNullable(restClientUtil.getElClient()
                    .search(sr, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
            return Optional.empty();
        }
    }
    
    public  Optional<SearchRequest> getSearchRequest(List<Group> groups,SearchSourceBuilder sb,IndexNameSuffix suffix){
        List<String> lst = groups.stream()
                .map(g -> queryUtils.indexName(g,suffix.suffix()))
                .filter(name -> name.isPresent())
                .map(name -> name.get())
                .collect(Collectors.toList());
        
        lst.forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        String[] indices = lst.stream().toArray(String[]::new);
        SearchRequest request = new SearchRequest(indices, sb);
        
        Arrays.stream(request.indices())
                .forEach(i -> LOG.log(Level.INFO, "--|| INDEX IN ARRAYS: {0}",i));
        return Optional.of(request);
    }
    
     public  Optional<SearchRequest> getTermCompleteRequest(List<Group> groups,SearchSourceBuilder sb){
        String[] indices = groups.stream()
                .map(g -> queryUtils.indexName(g,IndexNameSuffix.TERM_COMPLETION.suffix()))
                .filter(name -> name.isPresent())
                .map(name -> name.get())
                .toArray(String[]::new );
        Arrays.stream(indices)
                .forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        var sr = new SearchRequest(indices, sb);
       
        return Optional.of(sr);
    }
    
    public  Optional<SearchSourceBuilder> makeSearchSourceBuilder(QueryBuilder queryBuilder){
       var searchSourceBuilder = new SearchSourceBuilder();
       return Optional.of(searchSourceBuilder.query(queryBuilder));
    }
    
    public  Optional<SearchSourceBuilder> makeSearchSourceBuilder(SuggestBuilder suggestBuilder){
       var searchSourceBuilder = new SearchSourceBuilder();
       return Optional.of(searchSourceBuilder.suggest(suggestBuilder));
    }
     
    public Optional<SearchSourceBuilder> addSizeLimit(SearchSourceBuilder ssb,int size){
       return Optional.of(ssb.size(size));
    }
       
    public  Optional<SearchSourceBuilder> addHighlightBuilder(SearchSourceBuilder ssb,HighlightBuilder hb){
       return Optional.of(ssb.highlighter(hb));
    }
         
    public Optional<SearchSourceBuilder> addAggregate(SearchSourceBuilder ssb,AggregationBuilder aggb){
        return Optional.of(ssb.aggregation(aggb));
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
        
    public Optional<Terms> getTermsAggregations(SearchResponse sr,String termsValue){
      Terms terms =  sr.getAggregations().get(termsValue);
      LOG.log(Level.INFO, "-> RETURN TYPE: {0}", terms);
      return Optional.of(terms);
    }
    
    public List<Terms.Bucket> getBuckets(Terms terms){
        return (List<Terms.Bucket>) terms.getBuckets();
    }
       
    public List<TopHits> getTopHits(List<Terms.Bucket> buckets,String topHitsValue){
        return buckets.stream()
                .map(b -> topHits(b,topHitsValue))
                .filter(Optional::isPresent)
                .map(Optional::get).collect(toList());
    }
  
    private Optional<TopHits> topHits(Terms.Bucket bucket,String topHitsValue){
        try{
            return Optional.of((TopHits)bucket.getAggregations().get(topHitsValue));
        }catch(Exception ex){
            LOG.log(Level.SEVERE, "{0}", ex);
            return Optional.empty();
        }
    }
    
    public List<SearchHit> getTopSearchHits(List<TopHits> topHits){
        return topHits.stream().map(topSearchHits).flatMap(List::stream)
                .collect(toList());
                
    }
    
    private final Function<TopHits,List<SearchHit>> topSearchHits
            = (TopHits th) -> Arrays.stream(th.getHits().getHits()).collect(toList());
    
    public Optional<HighlightBuilder> makeHighlightBuilder(String field){
       HighlightBuilder highlightBuilder = new HighlightBuilder();
       HighlightBuilder.Field highlightContent =
               new HighlightBuilder.Field(field);
        highlightBuilder.field(highlightContent.numOfFragments(Constants.HIGHLIGHT_NUMBER_OF_FRAGMENTS)
                                .preTags(Constants.HIGHLIGHT_PRE_TAG)
                                .postTags(Constants.HIGHLIGHT_POST_TAG));
        return Optional.of(highlightBuilder);
   }
    
}
