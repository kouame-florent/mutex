/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import quantum.functional.api.Result;
import quantum.mutex.domain.type.Fragment;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.util.AggregationProperty;
import quantum.mutex.util.Constants;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.EnvironmentUtils;
import quantum.mutex.util.FragmentProperty;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.VirtualPageProperty;

/**
 *
 * @author Florent
 */
@Stateless
public class SearchVirtualPageService{

    private static final Logger LOG = Logger.getLogger(SearchVirtualPageService.class.getName());
    
    @Inject SearchCoreService scs;
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils envUtils;
    @Inject ElApiUtil elApiUtil;
    
    public Set<Fragment> search(List<Group> selectedGroups,String text){
        LOG.log(Level.INFO, "--> SELECTED GROUP : {0}", selectedGroups);  
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> processSearchStack(gps,text))
                    .getOrElse(() -> Collections.EMPTY_SET);
        }else{
            return processSearchStack(selectedGroups,text); 
        }
    }
    
    private Set<Fragment> processSearchStack(List<Group> groups,String text){
        Set<Fragment> termFragments = searchForMatch(groups,text);
        if(termFragments.size() < Constants.SEARCH_RESULT_THRESHOLD){
           Set<Fragment> phraseFragments = searchForMatchPhrase(groups, text);
           return Stream.concat(termFragments.stream(),phraseFragments.stream())
                   .collect(Collectors.toSet());
        }
        return termFragments;
    }
  
    private Set<Fragment> searchForMatch(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = searchMatchQueryBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(qb -> scs.makeSearchSourceBuilder(qb))
                .flatMap(ssb -> scs.addSizeLimit(ssb, 0))
                .flatMap(ssb -> makeTermsAggregationBuilder().flatMap(tab -> scs.addAggregate(ssb, tab)))
                .flatMap(ssb -> scs.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> scs.search(sr));
        Set<Fragment> fragments = rResponse.map(r -> extractFragments(r))
                .getOrElse(() -> Collections.EMPTY_SET);
        
        LOG.log(Level.INFO, "-->< FRAGMENTS SIZE: {0}", fragments.size());
        return fragments;
   }
    
    private Set<Fragment> searchForMatchPhrase(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = searchMatchPhraseQueryBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(qb -> scs.makeSearchSourceBuilder(qb))
                .flatMap(ssb -> scs.addSizeLimit(ssb, 0))
                .flatMap(ssb -> makeTermsAggregationBuilder().flatMap(tab -> scs.addAggregate(ssb, tab)))
                .flatMap(ssb -> scs.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> scs.search(sr));
        return rResponse.map(r -> extractFragments(r))
                .getOrElse(() -> Collections.EMPTY_SET);
    }
    
    public Set<Fragment> extractFragments(SearchResponse searchResponse){
        List<SearchHit> hits = scs.getTermsAggregations(searchResponse,
                AggregationProperty.PAGE_TERMS_VALUE.value())
            .map(t -> scs.getBuckets(t))
            .map(bs -> scs.getTopHits(bs,AggregationProperty.PAGE_TOP_HITS_VALUE.value()))
            .map(ths -> scs.getSearchHits(ths))
            .getOrElse(() -> Collections.EMPTY_LIST);
      
        LOG.log(Level.INFO,"--<> HITS SIZE: {0}" ,hits.size());
        
        return toFragments(hits);
    }
     
    public Set<String> analyze(String text){
        return Collections.EMPTY_SET;
    }
    
    private Result<QueryBuilder> searchMatchQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(property, text).fuzziness(Fuzziness.AUTO));
        return Result.of(query);
    }
   
    private Result<QueryBuilder> searchMatchPhraseQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(property, text)
                        .slop(Constants.QUERY_MATCH_PHRASE_SLOP));
        return Result.of(query);
    }
        
    private String getHighlighted( SearchHit hit){
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlight = highlightFields.get(FragmentProperty.CONTENT.value()); 
        return Arrays.stream(highlight.getFragments()).map(t -> t.string())
                .collect(Collectors.joining("..."));
    }
     
    private Set<Fragment> toFragments(List<SearchHit> hits){
        return hits.stream().map(h -> newFragment(h))
                .collect(Collectors.toSet());
    }
    
    private Fragment newFragment(SearchHit hit){
        Fragment frag = new Fragment.Builder()
            .content(getHighlighted(hit))
            .fileName((String)hit.getSourceAsMap().get(FragmentProperty.FILE_NAME.value()))
            .inodeUUID((String)hit.getSourceAsMap().get(FragmentProperty.INODE_UUID.value()))
            .pageIndex(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_INDEX.value())))
            .pageUUID((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_UUID.value()))
            .totalPageCount(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.TOTAL_PAGE_COUNT.value())))
            .build();
        
       return frag;
    }
   
    private Result<AggregationBuilder> makeTermsAggregationBuilder(){
        HighlightBuilder hlb = scs.getHighlightBuilder(VirtualPageProperty.CONTENT.value())
                .getOrElse(() -> new HighlightBuilder() );
        AggregationBuilder aggregation = AggregationBuilders
            .terms(AggregationProperty.PAGE_TERMS_VALUE.value())
                .field(AggregationProperty.PAGE_FIELD_VALUE.value())
            .subAggregation(
                AggregationBuilders.topHits(AggregationProperty.PAGE_TOP_HITS_VALUE.value())
                   .highlighter(hlb)
                   .size(2)
                   .from(0)
            );
        return Result.of(aggregation);
   }
}
