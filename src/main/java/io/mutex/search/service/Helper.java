/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;

import io.mutex.index.valueobject.AggregationProperty;
import io.mutex.index.service.RestClientUtilImpl;
import io.mutex.index.service.IndexNameUtils;
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
import io.mutex.search.valueobject.FragmentProperty;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.index.valueobject.VirtualPageProperty;
import io.mutex.search.valueobject.AlgoPriority;
import io.mutex.search.valueobject.Fragment;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;


/**
 *
 * @author Florent
 */
@Stateless
public class Helper {

    private static final Logger LOG = Logger.getLogger(Helper.class.getName());
    
    @Inject RestClientUtilImpl restClientUtil;
    @Inject IndexNameUtils queryUtils;
    @Inject LanguageService searchLanguageService;
    
    public Optional<SearchRequest> searchRequestBuilder(@NotNull List<Group> groups,String text,
            Function<String,QueryBuilder> queryBuilder){
        
        return  Optional.ofNullable(text)
                    .map(txt -> queryBuilder.apply(txt) )
                    .map(qb -> searchSourceBuilder(qb, 0))
                    .map(ssb -> addAggregate(ssb, topHitAggregationBuilder()))
                    .map(ssb -> searchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
    }
   
    public Optional<SearchResponse> search(SearchRequest sr){
        try {
            return Optional.ofNullable(restClientUtil.getElClient()
                    .search(sr, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
            return Optional.empty();
        }
    }
    
    public  SearchRequest searchRequest(List<Group> groups,SearchSourceBuilder sb,IndexNameSuffix suffix){
        List<String> lst = groups.stream()
                .map(g -> queryUtils.getName(g,suffix.suffix()))
                .filter(name -> name.isPresent())
                .map(name -> name.get())
                .collect(Collectors.toList());
        
        lst.forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        String[] indices = lst.stream().toArray(String[]::new);
        SearchRequest request = new SearchRequest(indices, sb);
        
        Arrays.stream(request.indices())
                .forEach(i -> LOG.log(Level.INFO, "--|| INDEX IN ARRAYS: {0}",i));
        return request;
    }
    
    public SearchRequest getTermCompleteRequest(List<Group> groups,SearchSourceBuilder sb){
        String[] indices = groups.stream()
                .map(g -> queryUtils.getName(g,IndexNameSuffix.TERM_COMPLETION.suffix()))
                .filter(name -> name.isPresent())
                .map(name -> name.get())
                .toArray(String[]::new );
        Arrays.stream(indices)
                .forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        var sr = new SearchRequest(indices, sb);
       
        return sr;
    }
    
    public  SearchSourceBuilder searchSourceBuilder(QueryBuilder queryBuilder,int sizeLimit){
       var searchSourceBuilder = new SearchSourceBuilder();
       return searchSourceBuilder.query(queryBuilder).size(sizeLimit);
    }
    
    public  SearchSourceBuilder searchSourceBuilder(QueryBuilder queryBuilder){
       var searchSourceBuilder = new SearchSourceBuilder();
       return searchSourceBuilder.query(queryBuilder);
    }
    
    public SearchSourceBuilder searchSourceBuilder(SuggestBuilder suggestBuilder){
       var searchSourceBuilder = new SearchSourceBuilder();
       return searchSourceBuilder.suggest(suggestBuilder);
    }
     
    public SearchSourceBuilder addSizeLimit(SearchSourceBuilder ssb,int size){
       return ssb.size(size);
    }
       
    public SearchSourceBuilder addHighlightBuilder(SearchSourceBuilder ssb,HighlightBuilder hb){
       return ssb.highlighter(hb);
    }
         
    public SearchSourceBuilder addAggregate(SearchSourceBuilder ssb,AggregationBuilder aggb){
        return ssb.aggregation(aggb);
    }
    
    public List<SearchHit> getSearchHits(SearchResponse sr){
       SearchHits hits = sr.getHits();
       return Arrays.stream(hits.getHits()).collect(Collectors.toList());
    }
    
    public List<SearchHit> searchHits(TopHits topHits){
       return Arrays.stream(topHits.getHits().getHits())
               .collect(Collectors.toList());
    }
    
    public Set<SearchHit> getSearchHits(List<TopHits> topHits){
       return topHits.stream().map(this::searchHits)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
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
    
    public HighlightBuilder makeHighlightBuilder(String field){
       HighlightBuilder highlightBuilder = new HighlightBuilder();
       HighlightBuilder.Field highlightContent =
               new HighlightBuilder.Field(field);
        highlightBuilder.field(highlightContent.numOfFragments(Constants.HIGHLIGHT_NUMBER_OF_FRAGMENTS)
                                .preTags(Constants.HIGHLIGHT_PRE_TAG)
                                .postTags(Constants.HIGHLIGHT_POST_TAG));
        return highlightBuilder;
    }
    
    public String contentMappingProperty(){
        if(searchLanguageService.getCurrentLanguage() == SupportedLanguage.FRENCH){
            return VirtualPageProperty.CONTENT_FR.value();
        }
        return VirtualPageProperty.CONTENT_EN.value();
    }
       
    private AggregationBuilder topHitAggregationBuilder(){
        HighlightBuilder hlb = makeHighlightBuilder(contentMappingProperty());
        AggregationBuilder aggregation = AggregationBuilders
            .terms(AggregationProperty.PAGE_TERMS_VALUE.value()).size(Constants.TOP_HITS_AGRREGATE_BUCKETS_NUMBER)
                .field(AggregationProperty.PAGE_FIELD_VALUE.value())
            .subAggregation(
                AggregationBuilders.topHits(AggregationProperty.PAGE_TOP_HITS_VALUE.value())
                   .highlighter(hlb)
                   .from(0)
                   .size(Constants.TOP_HITS_PER_FILE)
                   
            );
        return aggregation;
    }
        
    public Set<Fragment> extractFragments(SearchResponse searchResponse,AlgoPriority algoPriority){
       Set<SearchHit> hits = getTermsAggregations(searchResponse,AggregationProperty.PAGE_TERMS_VALUE.value())
            .map(t -> getBuckets(t))
            .map(bs -> getTopHits(bs,AggregationProperty.PAGE_TOP_HITS_VALUE.value()))
            .map(ths -> getSearchHits(ths))
            .orElseGet(() -> Collections.EMPTY_SET);
      
        LOG.log(Level.INFO,"--<> HITS SIZE: {0}" ,hits.size());
        
        return toFragments(hits,algoPriority);
    }
    
    private Set<Fragment> toFragments(Set<SearchHit> hits,AlgoPriority algoPriority){
        return hits.stream().map(h -> newFragment(h,algoPriority))
                .collect(Collectors.toSet());
    }
    
    private Fragment newFragment(SearchHit hit,AlgoPriority algoPriority){
        
        Fragment frag = new Fragment.Builder()
            .content(getHighlighted(hit))
            .fileName((String)hit.getSourceAsMap().get(FragmentProperty.FILE_NAME.property()))
            .inodeUUID((String)hit.getSourceAsMap().get(FragmentProperty.INODE_UUID.property()))
            .pageIndex(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_INDEX.property())))
            .pageUUID((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_UUID.property()))
            .totalPageCount(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.TOTAL_PAGE_COUNT.property())))
            .score(hit.getScore())
            .algoPriority(algoPriority)
            .build();
        
       return frag;
    }
     
    private String getHighlighted( SearchHit hit){
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlight = highlightFields.get(getFragmentContentProperty()); 
        return Arrays.stream(highlight.getFragments()).map(t -> t.string())
                .collect(Collectors.joining("..."));
    }
    
    private String getFragmentContentProperty(){
        if(searchLanguageService.getCurrentLanguage() == SupportedLanguage.FRENCH){
            return FragmentProperty.CONTENT_FR.property();
        }
        return FragmentProperty.CONTENT_EN.property();
    }
 
}
