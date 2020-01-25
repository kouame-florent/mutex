/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;

import io.mutex.index.service.VirtualPageService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import io.mutex.search.valueobject.Fragment;
import io.mutex.user.entity.Group;
import io.mutex.index.valueobject.AggregationProperty;
import io.mutex.index.valueobject.Constants;
import io.mutex.index.valueobject.ElApiUtil;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.index.valueobject.FragmentProperty;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.user.service.UserGroupService;


/**
 *
 * @author Florent
 */
@Stateless
public class SearchVirtualPageService{

    private static final Logger LOG = Logger.getLogger(SearchVirtualPageService.class.getName());
    
    @Inject SearchHelper searchHelper;
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils envUtils;
    @Inject ElApiUtil elApiUtil;
    @Inject VirtualPageService virtualPageService;
    @Inject SearchLanguageService searchLanguageService;
    
    public List<Fragment> search(List<Group> selectedGroups,String text){
        LOG.log(Level.INFO, "--> SELECTED GROUP : {0}", selectedGroups);  
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> processSearchStack(gps,text))
                    .orElseGet(() -> Collections.EMPTY_LIST);
        }else{
            return processSearchStack(selectedGroups,text); 
        }
    }
    
    private List<Fragment> processSearchStack(List<Group> groups,String text){
        
        List<Fragment> phraseFragments = matchPhrases(groups, text);
//        if(phraseFragments.size() < Constants.SEARCH_RESULT_THRESHOLD){
//           List<Fragment> termFragments = matchTerms(groups,text);
//           return Stream.concat(phraseFragments.stream(),termFragments.stream())
//                   .collect(Collectors.toList());
//        }
        return phraseFragments;
    }
  
    private List<Fragment> matchTerms(List<Group> groups,String text){
        Optional<SearchRequest> rSearchRequest = termQueryBuilder(virtualPageService.getContentMappingProperty(), text) 
                .flatMap(qb -> searchHelper.getSearchSourceBuilder(qb))
                .flatMap(ssb -> searchHelper.addSizeLimit(ssb, 0))
                .flatMap(ssb -> makeTermsAggregationBuilder().flatMap(tab -> searchHelper.addAggregate(ssb, tab)))
                .flatMap(ssb -> searchHelper.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> searchHelper.search(sr));  
        List<Fragment> fragments = rResponse.map(r -> extractFragments(r))
                .orElseGet(() -> Collections.EMPTY_LIST);
        
        LOG.log(Level.INFO, "-->< FRAGMENTS SIZE: {0}", fragments.size());
        return fragments;
   }
    
    private List<Fragment> matchPhrases(List<Group> groups,String text){
        Optional<SearchRequest> rSearchRequest = phraseQueryBuilder(virtualPageService.getContentMappingProperty(), text)
                .flatMap(qb -> searchHelper.getSearchSourceBuilder(qb))
                .flatMap(ssb -> searchHelper.addSizeLimit(ssb, 0))
                .flatMap(ssb -> makeTermsAggregationBuilder().flatMap(tab -> searchHelper.addAggregate(ssb, tab)))
                .flatMap(ssb -> searchHelper.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> searchHelper.search(sr));
        return rResponse.map(r -> extractFragments(r))
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    public List<Fragment> extractFragments(SearchResponse searchResponse){
        List<SearchHit> hits = searchHelper.getTermsAggregations(searchResponse,
                AggregationProperty.PAGE_TERMS_VALUE.value())
            .map(t -> searchHelper.getBuckets(t))
            .map(bs -> searchHelper.getTopHits(bs,AggregationProperty.PAGE_TOP_HITS_VALUE.value()))
            .map(ths -> searchHelper.getSearchHits(ths))
            .orElseGet(() -> Collections.EMPTY_LIST);
      
        LOG.log(Level.INFO,"--<> HITS SIZE: {0}" ,hits.size());
        
        return toFragments(hits);
    }
    

     
//    public Set<String> analyze(String text){
//        return Collections.EMPTY_SET;
//    }
    
    private Optional<QueryBuilder> termQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(property, text));
                        //.fuzziness(Fuzziness.AUTO));
        return Optional.of(query);
    }
   
    private Optional<QueryBuilder> phraseQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(property, text));
//                        .slop(Constants.QUERY_MATCH_PHRASE_SLOP));
        return Optional.of(query);
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
     
    private List<Fragment> toFragments(List<SearchHit> hits){
        return hits.stream().map(h -> newFragment(h))
                .collect(Collectors.toList());
    }
    
    private Fragment newFragment(SearchHit hit){
        Fragment frag = new Fragment.Builder()
            .content(getHighlighted(hit))
            .fileName((String)hit.getSourceAsMap().get(FragmentProperty.FILE_NAME.property()))
            .inodeUUID((String)hit.getSourceAsMap().get(FragmentProperty.INODE_UUID.property()))
            .pageIndex(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_INDEX.property())))
            .pageUUID((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_UUID.property()))
            .totalPageCount(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.TOTAL_PAGE_COUNT.property())))
            .build();
        
       return frag;
    }
   
    private Optional<AggregationBuilder> makeTermsAggregationBuilder(){
        HighlightBuilder hlb = searchHelper.makeHighlightBuilder(virtualPageService.getContentMappingProperty())
                .orElseGet(() -> new HighlightBuilder() );
        AggregationBuilder aggregation = AggregationBuilders
            .terms(AggregationProperty.PAGE_TERMS_VALUE.value())
                .field(AggregationProperty.PAGE_FIELD_VALUE.value())
            .subAggregation(
                AggregationBuilders.topHits(AggregationProperty.PAGE_TOP_HITS_VALUE.value())
                   .highlighter(hlb)
                   .size(Constants.TOP_HITS_PER_FILE)
                   .from(0)
            );
        return Optional.of(aggregation);
   }
}
