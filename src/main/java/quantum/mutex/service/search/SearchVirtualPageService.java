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
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.Fragment;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.Constants;
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
    
    @Inject SearchCoreService coreSearchService;

    public Set<Fragment> searchForMatch(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = searchMatchQueryBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(qb -> coreSearchService.getSearchSourceBuilder(qb))
                .flatMap(ssb -> highlightBuilder().flatMap(hlb -> coreSearchService.provideHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> coreSearchService.search(sr));
        
        List<SearchHit> hits = rResponse.map(sr -> coreSearchService.getSearchHits(sr))
                .getOrElse(() -> Collections.EMPTY_LIST);
        return toFragments(hits);
    }
    
    public Set<Fragment> searchForMatchPhrase(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = searchMatchPhraseQueryBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(qb -> coreSearchService.getSearchSourceBuilder(qb))
                .flatMap(ssb -> highlightBuilder().flatMap(hlb -> coreSearchService.provideHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> coreSearchService.search(sr));
        
        List<SearchHit> hits = rResponse.map(sr -> coreSearchService.getSearchHits(sr))
                .getOrElse(() -> Collections.EMPTY_LIST);
        return toFragments(hits);
    }
    
    public Set<String> analyze(String text){
        return Collections.EMPTY_SET;
    }
    
    private Result<QueryBuilder> searchMatchQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(property, text));
                
//        LOG.log(Level.INFO, "--> SEARCH MATCH QUERY: {0}", query.toString());
        return Result.of(query);
    }
   
    private Result<QueryBuilder> searchMatchPhraseQueryBuilder(String property,String text){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(property, text));
                
//        LOG.log(Level.INFO, "--> SEARCH MATCH PHRASE QUERY: {0}", query.toString());
        return Result.of(query);
    }
        
    protected String getHighlighted(@NotNull SearchHit hit){
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField highlight = highlightFields.get(FragmentProperty.CONTENT.value()); 
        return Arrays.stream(highlight.getFragments()).map(t -> t.string())
                .collect(Collectors.joining("..."));
    }
     
    private Set<Fragment> toFragments(List<SearchHit> hits){
        return hits.stream().map(h -> toMutexFragment(h))
                .collect(Collectors.toSet());
    }
    
    private Fragment toMutexFragment(@NotNull SearchHit hit){
        Fragment f = new Fragment();
        f.setContent(getHighlighted(hit));
        f.setFileName((String)hit.getSourceAsMap().get(FragmentProperty.FILE_NAME.value()));
        f.setInodeUUID((String)hit.getSourceAsMap().get(FragmentProperty.INODE_UUID.value()));
        f.setPageIndex(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_INDEX.value())));
        f.setPageUUID((String)hit.getSourceAsMap().get(FragmentProperty.PAGE_UUID.value()));
        f.setTotalPageCount(Integer.valueOf((String)hit.getSourceAsMap().get(FragmentProperty.TOTAL_PAGE_COUNT.value())));
        return f;
    }
   
    private Result<HighlightBuilder> highlightBuilder(){
       HighlightBuilder highlightBuilder = new HighlightBuilder();
       HighlightBuilder.Field highlightContent =
               new HighlightBuilder.Field(VirtualPageProperty.CONTENT.value());
        highlightBuilder.field(highlightContent.numOfFragments(Constants.HIGHLIGHT_NUMBER_OF_FRAGMENTS)
                                .preTags(Constants.HIGHLIGHT_PRE_TAG)
                                .postTags(Constants.HIGHLIGHT_POST_TAG));
        return Result.of(highlightBuilder);
   }
  
}
