/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.Constants;
import quantum.mutex.util.VirtualPageProperty;

/**
 *
 * @author Florent
 */
@Stateless
public class PreviewService extends SearchBaseService{

    private static final Logger LOG = Logger.getLogger(PreviewService.class.getName());
    
//    @Inject ApiClientUtils acu;
//    @Inject QueryUtils elasticQueryUtils;
//    
    public Result<VirtualPage> previewForMatchPhrase(List<Group> groups,String text,String pageUUID){

        Result<SearchRequest> rSearchRequest = previewPhraseQueryBuilder(VirtualPageProperty.CONTENT.value(),
                            text, pageUUID)
                .flatMap(qb -> getSearchSourceBuilder(qb))
                .flatMap(ssb -> highlightBuilder().flatMap(hlb -> provideHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> getSearchRequest(groups,ssb));
        
        return rSearchRequest.flatMap(rs -> preview(rs));
   }
    
    public Result<VirtualPage> previewForMatch(List<Group> groups,String text,String pageUUID){
        
        Result<SearchRequest> rSearchRequest = previewMatchQueryBuilder(VirtualPageProperty.CONTENT.value(),
                            text, pageUUID)
                .flatMap(qb -> getSearchSourceBuilder(qb))
                .flatMap(ssb -> highlightBuilder().flatMap(hlb -> provideHighlightBuilder(ssb, hlb)))
                .flatMap(ssb -> getSearchRequest(groups,ssb));
        
        return rSearchRequest.flatMap(rs -> preview(rs));
   }
    
    private Result<VirtualPage> preview(SearchRequest searchRequest){
        
        Result<SearchResponse> rSearchResponse =  search(searchRequest);
        
        rSearchResponse
                .forEach(sr -> LOG.log(Level.INFO, "--> RESPONSE STATUS: {0}",
                        sr.status()));
        
        List<SearchHit> searchHits = rSearchResponse
                .filter(sr -> sr.status().equals(RestStatus.OK))
                .map(sr -> getSearchHits(sr))
                .getOrElse(Collections.EMPTY_LIST);
       
        Result<VirtualPage> rVP =  searchHits.stream().map(h -> toVirtualPage(h))
                .filter(Result::isSuccess)
                .findFirst().orElseGet(() -> Result.empty());
       
        Result<String> rContent = getHighlightContent(searchHits);
        
        Result<VirtualPage> withHighlight = 
            rContent.flatMap(c -> rVP.flatMap(vp -> setHighlightedContent(vp, c)));
        
        return withHighlight;
    }
    
//    private List<SearchHit> getSearchHits(SearchResponse sr){
//       SearchHits hits = sr.getHits();
//       return Arrays.stream(hits.getHits()).collect(Collectors.toList());
//    }
    
    private Result<String> getHighlightContent(List<SearchHit> hits){
        Optional<String> res = hits.stream().map(h -> h.getHighlightFields())
                .map(mf -> mf.get(VirtualPageProperty.CONTENT.value()))
                .map(hf -> hf.getFragments())
                .map(txs -> txs[0].toString())
                .findAny();
        
        return res.map(r -> Result.of(r)).orElseGet(Result::empty);
    }
    
    private Result<VirtualPage> setHighlightedContent(VirtualPage vp, String hlc){
        vp.setContent(hlc);
        return Result.of(vp);
    }
    
//    private Result<SearchResponse> search(SearchRequest sr){
//        try {
//            return Result.success(acu.getRestHighLevelClient()
//                    .search(sr, RequestOptions.DEFAULT));
//        } catch (IOException ex) {
//            return Result.failure(ex);
//        }
//    }
     
    private Result<QueryBuilder> previewPhraseQueryBuilder(String property,String phrase,String pageUUID){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(property, phrase))
                .filter(QueryBuilders.termQuery(VirtualPageProperty.PAGE_UUID.value(),pageUUID));
        LOG.log(Level.INFO, "--> PREVIEW QUERY: {0}", query.toString());
        return Result.of(query);
    }
    
    private Result<QueryBuilder> previewMatchQueryBuilder(String property,String phrase,String pageUUID){
        var query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(property, phrase))
                .filter(QueryBuilders.termQuery(VirtualPageProperty.PAGE_UUID.value(),pageUUID));
        LOG.log(Level.INFO, "--> PREVIEW QUERY: {0}", query.toString());
        return Result.of(query);
    }
    
//    private Result<SearchSourceBuilder> getSearchSourceBuilder(QueryBuilder qb){
//       var ssb = new SearchSourceBuilder();
//       return Result.of(ssb.query(qb));
//    }
//    
//   private Result<SearchSourceBuilder> provideHighlightBuilder(SearchSourceBuilder ssb,HighlightBuilder hb){
//       ssb.highlighter(hb);
//       return Result.of(ssb);
//   }
   
   private Result<HighlightBuilder> highlightBuilder(){
       HighlightBuilder highlightBuilder = new HighlightBuilder();
       HighlightBuilder.Field highlightContent =
               new HighlightBuilder.Field(VirtualPageProperty.CONTENT.value());
        highlightBuilder.field(highlightContent.numOfFragments(0)
                                .preTags(Constants.HIGHLIGHT_PRE_TAG)
                                .postTags(Constants.HIGHLIGHT_POST_TAG));
        return Result.of(highlightBuilder);
   }
    
//    private Result<SearchRequest> getSearchRequest(List<Group> groups,SearchSourceBuilder sb){
//        String[] indices = groups.stream()
//                .map(g -> elasticQueryUtils.getVirtualPageIndexName(g))
//                .toArray(String[]::new);
//        Arrays.stream(indices)
//                .forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
//        var sr = new SearchRequest(indices, sb);
//       
//        return Result.of(sr);
//    }
   
    private Result<VirtualPage> toVirtualPage(SearchHit hit){
        
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//        LOG.log(Level.INFO, "--> VP PAGE UUID: {0}", sourceAsMap.get(VirtualPageProperty.PAGE_INDEX.value()));
        VirtualPage vp = new VirtualPage();
        try{
            vp.setUuid((String)sourceAsMap.get(VirtualPageProperty.PAGE_UUID.value()));
            vp.setInodeUUID((String)sourceAsMap.get(VirtualPageProperty.INODE_UUID.value()));
            vp.setFileName((String)sourceAsMap.get(VirtualPageProperty.FILE_NAME.value()));
            vp.setContent((String)sourceAsMap.get(VirtualPageProperty.CONTENT.value()));
            vp.setPageIndex(Integer.valueOf((String)sourceAsMap.get(VirtualPageProperty.PAGE_INDEX.value())));
            vp.setTotalPageCount(Integer.valueOf((String)sourceAsMap.get(VirtualPageProperty.TOTAL_PAGE_COUNT.value())));
            return Result.success(vp);
        }catch(NumberFormatException exception){
            return Result.failure(exception);
        }
    }

}
