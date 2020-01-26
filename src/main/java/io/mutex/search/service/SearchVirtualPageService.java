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
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.elasticsearch.common.unit.Fuzziness;


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
    
    public Set<Fragment> search(@NotNull List<Group> selectedGroups,String text){
        LOG.log(Level.INFO, "--> SELECTED GROUP : {0}", selectedGroups);  
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> processSearchStack(gps,text))
                    .orElseGet(() -> Collections.EMPTY_SET);
        }else{
            return processSearchStack(selectedGroups,text); 
        }
    }
    
    private Set<Fragment> processSearchStack(@NotNull List<Group> groups,String text){
        
        Set<Fragment> phraseFragments = matchPhrase(groups, text);
//        List<Fragment> phraseFragments = Collections.EMPTY_LIST;
        
        Set<Fragment> termFragments = match(groups,text);
        return prioritizeResult(text, phraseFragments, termFragments);

    }
    
    private Set<Fragment> prioritizeResult(String text,Set<Fragment> phraseFragments,
            Set<Fragment> termFragments){
        
        String[] searchTexts = text.split("\\s+");
        
        LOG.log(Level.INFO, "--> SEARCH TEXT LEN: {0}", searchTexts.length);
        
       if(searchTexts.length == 1){
           return Stream.concat(termFragments.stream(),phraseFragments.stream())
                   .collect(Collectors.toSet());
       } 
       return Stream.concat(phraseFragments.stream(),termFragments.stream())
                   .collect(Collectors.toSet());
    }
    
    private Set<Fragment> matchPhrase(@NotNull List<Group> groups,String text){
        
        Optional<SearchRequest> rSearchRequest = 
                Optional.ofNullable(text)
                    .map(txt -> phraseQueryBuilder(virtualPageService.getContentMappingProperty(), txt))
                    .map(qb -> searchHelper.searchSourceBuilder(qb, 0))
                    .map(ssb -> searchHelper.addAggregate(ssb, topHitAggregationBuilder()))
                    .map(ssb -> searchHelper.searchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));

        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> searchHelper.search(sr));
        return rResponse.map(r -> extractFragments(r))
                .orElseGet(() -> Collections.EMPTY_SET);
    }
    
    private QueryBuilder phraseQueryBuilder(String property,String text){
       QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(property, text));
//                        .slop(Constants.QUERY_MATCH_PHRASE_SLOP));
        return query;
    }
  
    private Set<Fragment> match(List<Group> groups,String text){
        
           Optional<SearchRequest> rSearchRequest = 
                Optional.ofNullable(text)
                    .map(txt -> matchQueryBuilder(virtualPageService.getContentMappingProperty(), txt))
                    .map(qb -> searchHelper.searchSourceBuilder(qb, 0))
                    .map(ssb -> searchHelper.addAggregate(ssb, topHitAggregationBuilder()))
                    .map(ssb -> searchHelper.searchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
       
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> searchHelper.search(sr));  
        Set<Fragment> fragments = rResponse.map(r -> extractFragments(r))
                .orElseGet(() -> Collections.EMPTY_SET);
        
        LOG.log(Level.INFO, "-->< FRAGMENTS SIZE: {0}", fragments.size());
        return fragments;
    }
        
    public Set<Fragment> extractFragments(SearchResponse searchResponse){
        Set<SearchHit> hits = searchHelper.getTermsAggregations(searchResponse,
                AggregationProperty.PAGE_TERMS_VALUE.value())
            .map(t -> searchHelper.getBuckets(t))
            .map(bs -> searchHelper.getTopHits(bs,AggregationProperty.PAGE_TOP_HITS_VALUE.value()))
            .map(ths -> searchHelper.getSearchHits(ths))
            .orElseGet(() -> Collections.EMPTY_SET);
      
        LOG.log(Level.INFO,"--<> HITS SIZE: {0}" ,hits.size());
        
        return toFragments(hits);
    }
  
    
    private QueryBuilder matchQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(property, text));
                        //.fuzziness(1));
        return query;
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
     
    private Set<Fragment> toFragments(Set<SearchHit> hits){
        return hits.stream().map(h -> newFragment(h))
                .collect(Collectors.toSet());
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
   
    private AggregationBuilder topHitAggregationBuilder(){
        HighlightBuilder hlb = searchHelper.makeHighlightBuilder(virtualPageService.getContentMappingProperty());
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
}
