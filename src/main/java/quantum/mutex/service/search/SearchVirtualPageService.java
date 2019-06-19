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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.Fragment;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.domain.UserGroupService;
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
    
    @Inject SearchCoreService css;
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
                .flatMap(qb -> css.getSearchSourceBuilder(qb))
                .flatMap(ssb -> css.addSizeLimit(ssb, 0))
                .flatMap(ssb -> makeHighlightBuilder().flatMap(hlb -> css.provideHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> makeTermsAggregationBuilder().flatMap(tab -> css.provideAggregate(ssb, tab)))
                .flatMap(ssb -> css.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        rSearchRequest.forEachOrException(elApiUtil::logJson)
                .forEach(e -> LOG.log(Level.SEVERE, "{0}", e));
        
//        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> css.search(sr));
//        
//        List<SearchHit> hits = rResponse.map(sr -> css.getSearchHits(sr))
//                .getOrElse(() -> Collections.EMPTY_LIST);
//        return toFragments(hits);
            return Collections.EMPTY_SET;
    }
    
    private Set<Fragment> searchForMatchPhrase(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = searchMatchPhraseQueryBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(qb -> css.getSearchSourceBuilder(qb))
                .flatMap(ssb -> makeHighlightBuilder().flatMap(hlb -> css.provideHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> css.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
//        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> css.search(sr));
//        
//        List<SearchHit> hits = rResponse.map(sr -> css.getSearchHits(sr))
//                .getOrElse(() -> Collections.EMPTY_LIST);
//        return toFragments(hits);
        return Collections.EMPTY_SET;
    }
     
    public Set<String> analyze(String text){
        return Collections.EMPTY_SET;
    }
    
    private Result<QueryBuilder> searchMatchQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(property, text));
        return Result.of(query);
    }
   
    private Result<QueryBuilder> searchMatchPhraseQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(property, text));
        return Result.of(query);
    }
        
    private String getHighlighted( SearchHit hit){
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlight = highlightFields.get(FragmentProperty.CONTENT.value()); 
        return Arrays.stream(highlight.getFragments()).map(t -> t.string())
                .collect(Collectors.joining("..."));
    }
     
    private Set<Fragment> toFragments(List<SearchHit> hits){
        return hits.stream().map(h -> toMutexFragment(h))
                .collect(Collectors.toSet());
    }
    
    private Fragment toMutexFragment( SearchHit hit){
        Fragment f = new Fragment();
        f.setContent(getHighlighted(hit));
        f.setFileName((String)hit.getSourceAsMap().get(FragmentProperty.FILE_NAME.value()));
        f.setInodeUUID((String)hit.getSourceAsMap().get(FragmentProperty.INODE_UUID.value()));
        f.setPageIndex(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_INDEX.value())));
        f.setPageUUID((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_UUID.value()));
        f.setTotalPageCount(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.TOTAL_PAGE_COUNT.value())));
        return f;
    }
   
    private Result<HighlightBuilder> makeHighlightBuilder(){
       HighlightBuilder highlightBuilder = new HighlightBuilder();
       HighlightBuilder.Field highlightContent =
               new HighlightBuilder.Field(VirtualPageProperty.CONTENT.value());
        highlightBuilder.field(highlightContent.numOfFragments(Constants.HIGHLIGHT_NUMBER_OF_FRAGMENTS)
                                .preTags(Constants.HIGHLIGHT_PRE_TAG)
                                .postTags(Constants.HIGHLIGHT_POST_TAG));
        return Result.of(highlightBuilder);
   }
    
    private Result<AggregationBuilder> makeTermsAggregationBuilder(){
        HighlightBuilder hlb = makeHighlightBuilder().getOrElse(() -> new HighlightBuilder() );
        AggregationBuilder aggregation = AggregationBuilders.terms("top_virtual_pages")
            .field("inode_uuid")
            .subAggregation(
                AggregationBuilders.topHits("top_hits")
                   .highlighter(hlb)
                   .size(2)
                   .from(0)
            );
        return Result.of(aggregation);
   }
      
}
