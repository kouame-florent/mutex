/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;


import io.mutex.index.service.VirtualPageService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import io.mutex.search.valueobject.Fragment;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.user.entity.Group;
import io.mutex.index.valueobject.Constants;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.index.valueobject.VirtualPageProperty;
import io.mutex.user.service.UserGroupService;


/**
 *
 * @author Florent
 */
@Stateless
public class PreviewService{

    private static final Logger LOG = Logger.getLogger(PreviewService.class.getName());
    
    @Inject SearchHelper coreSearchService;
    @Inject EnvironmentUtils envUtils;
    @Inject UserGroupService userGroupService;
    @Inject VirtualPageService virtualPageService;
    
    public Optional<VirtualPage> prewiew(Fragment fragment,List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                .flatMap(gps -> processPreviewStack(gps,fragment,text));
        }else{
            return processPreviewStack(selectedGroups, fragment,text);
        }
    }
    
    public Optional<VirtualPage> processPreviewStack(List<Group> groups,Fragment fragment,String text){
        LOG.log(Level.INFO, "--> FRAGMENT PAGE UUID: {0}", fragment.getPageUUID());
     
        Optional<VirtualPage> rVirtualPage;
        rVirtualPage = searchWithMatchPhraseQuery(groups, text,fragment.getPageUUID());
        
        if(rVirtualPage.isEmpty()){
            rVirtualPage = searchWithMatchQuery(groups, text,fragment.getPageUUID());
        }
        return rVirtualPage;
    }
    
    public Optional<VirtualPage> searchWithMatchPhraseQuery(List<Group> groups,String text,String pageUUID){
        Optional<SearchRequest> rSearchRequest = previewPhraseQueryBuilder(virtualPageService.getContentMappingProperty(),
                            text, pageUUID)
                .flatMap(qb -> coreSearchService.getSearchSourceBuilder(qb))
                .flatMap(ssb -> highlightBuilder().flatMap(hlb -> coreSearchService.addHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        
        List<SearchHit> hits = rSearchRequest
                .map(r -> search(r)).orElseGet(() -> Collections.EMPTY_LIST);
        
        return toVirtualPage(hits)
                .flatMap(vp -> addHighLightedContent(hits, vp));
         
   }
    
    public Optional<VirtualPage> searchWithMatchQuery(List<Group> groups,String text,String pageUUID){
        Optional<SearchRequest> rSearchRequest = previewMatchQueryBuilder(virtualPageService.getContentMappingProperty(),
                            text, pageUUID)
                .flatMap(qb -> coreSearchService.getSearchSourceBuilder(qb))
                .flatMap(ssb -> highlightBuilder().flatMap(hlb -> coreSearchService.addHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        List<SearchHit> hits = rSearchRequest
                .map(r -> search(r)).orElseGet(() -> Collections.EMPTY_LIST);
        
        return toVirtualPage(hits)
                .flatMap(vp -> addHighLightedContent(hits, vp));
   }
    
    private List<SearchHit> search(SearchRequest searchRequest){
        
        Optional<SearchResponse> rSearchResponse =  coreSearchService.search(searchRequest);
        
        rSearchResponse
                .ifPresent(sr -> LOG.log(Level.INFO, "--> RESPONSE STATUS: {0}",
                        sr.status()));
        
        List<SearchHit> searchHits = rSearchResponse
                .filter(sr -> sr.status().equals(RestStatus.OK))
                .map(sr -> coreSearchService.getSearchHits(sr))
                .orElseGet(() -> Collections.EMPTY_LIST);
       
//        Optional<VirtualPage> rVP =  searchHits.stream().map(h -> toVirtualPage_(h))
//                .filter(Optional::isSuccess)
//                .findFirst().orElseGet(() -> Optional.empty());
//       
//        Optional<String> rContent = getHighlightedContent(searchHits);
//        
//        Optional<VirtualPage> withHighlight = 
//            rContent.flatMap(c -> rVP.flatMap(vp -> setHighlightedContent(vp, c)));
//        
//        return withHighlight;
        return searchHits;
    }
    
    private Optional<VirtualPage> toVirtualPage(List<SearchHit> searchHits){
        return searchHits.stream().map(h -> toVirtualPage_(h))
                .filter(o -> o.isPresent())
                .findFirst().orElseGet(() -> Optional.empty());
                
//                .filter(Optional::ifPresent)
//                .findFirst().orElseGet(() -> Optional.empty());
    }
    
    private Optional<VirtualPage> addHighLightedContent(List<SearchHit> searchHits,
            VirtualPage virtualPage){
        return getHighlightedContent(searchHits)
                .flatMap(c -> setHighlightedContent(virtualPage, c));
    }
    
    private Optional<String> getHighlightedContent(List<SearchHit> hits){
        Optional<String> res = hits.stream().map(h -> h.getHighlightFields())
                .map(mf -> mf.get(virtualPageService.getContentMappingProperty()))
                .map(hf -> hf.getFragments())
                .map(txs -> txs[0].toString())
                .findAny();
        
        return res.map(r -> Optional.of(r)).orElseGet(Optional::empty);
    }
    
    private Optional<VirtualPage> setHighlightedContent(VirtualPage vp, String hlc){
        vp.setContent(hlc);
        return Optional.of(vp);
    }
    
//    private Optional<SearchResponse> search(SearchRequest sr){
//        try {
//            return Optional.success(acu.getRestHighLevelClient()
//                    .search(sr, RequestOptions.DEFAULT));
//        } catch (IOException ex) {
//            return Optional.failure(ex);
//        }
//    }
     
    private Optional<QueryBuilder> previewPhraseQueryBuilder(String property,String phrase,String pageUUID){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(property, phrase))
                .filter(QueryBuilders.termQuery(VirtualPageProperty.PAGE_UUID.value(),pageUUID));
        LOG.log(Level.INFO, "--> PREVIEW QUERY: {0}", query.toString());
        return Optional.of(query);
    }
    
    private Optional<QueryBuilder> previewMatchQueryBuilder(String property,String term,String pageUUID){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(property, term))
                .filter(QueryBuilders.termQuery(VirtualPageProperty.PAGE_UUID.value(),pageUUID));
        LOG.log(Level.INFO, "--> PREVIEW QUERY: {0}", query.toString());
        return Optional.of(query);
    }
    
//    private Optional<SearchSourceBuilder> getSearchSourceBuilder(QueryBuilder qb){
//       var ssb = new SearchSourceBuilder();
//       return Optional.of(ssb.query(qb));
//    }
//    
//   private Optional<SearchSourceBuilder> provideHighlightBuilder(SearchSourceBuilder ssb,HighlightBuilder hb){
//       ssb.highlighter(hb);
//       return Optional.of(ssb);
//   }
   
   private Optional<HighlightBuilder> highlightBuilder(){
       HighlightBuilder highlightBuilder = new HighlightBuilder();
       HighlightBuilder.Field highlightContent =
               new HighlightBuilder.Field(virtualPageService.getContentMappingProperty());
        highlightBuilder.field(highlightContent.numOfFragments(0)
                                .preTags(Constants.HIGHLIGHT_PRE_TAG)
                                .postTags(Constants.HIGHLIGHT_POST_TAG));
        return Optional.of(highlightBuilder);
   }
    
//    private Optional<SearchRequest> getSearchRequest(List<Group> groups,SearchSourceBuilder sb){
//        String[] indices = groups.stream()
//                .map(g -> elasticQueryUtils.getVirtualPageIndexName(g))
//                .toArray(String[]::new);
//        Arrays.stream(indices)
//                .forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
//        var sr = new SearchRequest(indices, sb);
//       
//        return Optional.of(sr);
//    }
   
    private Optional<VirtualPage> toVirtualPage_(SearchHit hit){
        
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//        LOG.log(Level.INFO, "--> VP PAGE UUID: {0}", sourceAsMap.get(VirtualPageProperty.PAGE_INDEX.value()));
        VirtualPage vp = new VirtualPage();
        try{
            vp.setUuid((String)sourceAsMap.get(VirtualPageProperty.PAGE_UUID.value()));
            vp.setInodeUUID((String)sourceAsMap.get(VirtualPageProperty.INODE_UUID.value()));
            vp.setFileName((String)sourceAsMap.get(VirtualPageProperty.FILE_NAME.value()));
            vp.setContent((String)sourceAsMap.get(virtualPageService.getContentMappingProperty()));
            vp.setPageIndex(Integer.valueOf((String)sourceAsMap.get(VirtualPageProperty.PAGE_INDEX.value())));
            vp.setTotalPageCount(Integer.valueOf((String)sourceAsMap.get(VirtualPageProperty.TOTAL_PAGE_COUNT.value())));
            return Optional.of(vp);
        }catch(NumberFormatException ex){
            LOG.log(Level.SEVERE, "{0}",ex);
            return Optional.empty();
        }
    }

}
