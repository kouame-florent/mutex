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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.ServiceEndPoint;
import quantum.mutex.util.VirtualPageProperty;



/**
 *
 * @author Florent
 */
@Stateless
public class SearchService {

    private static final Logger LOG = Logger.getLogger(SearchService.class.getName());
    
    @Inject ApiClientUtils acu;
    @Inject QueryUtils elasticQueryUtils;
    
    public Result<String> searchForMatchPhrase(List<Group> group,String text){
        Result<String> json = elasticQueryUtils.matchPhraseQuery(text)
                .flatMap(jo -> elasticQueryUtils.addHighlighting(jo))
                .map(jo -> jo.toString());
                
        return getVirtualPagesUri.apply(group)
                    .flatMap(uri -> json.flatMap(js -> acu.post(uri, Entity.json(js),headers())))
                    .map(r -> r.readEntity(String.class));
    }
    
    public Result<String> searchForMatch(List<Group> group,String text){
        Result<String> json = elasticQueryUtils.matchQuery(text)
                .flatMap(jo -> elasticQueryUtils.addHighlighting(jo))
                .map(jo -> jo.toString());
                
        return getVirtualPagesUri.apply(group)
                    .flatMap(uri -> json.flatMap(js -> acu.post(uri, Entity.json(js),headers())))
                    .map(r -> r.readEntity(String.class));
    }
    
    public List<VirtualPage> searchForTermQuery(List<Group> groups,String fileUUID,int pageIndex){
        LOG.log(Level.INFO, "--> FILE UUID: {0}", fileUUID);
        LOG.log(Level.INFO, "--> PAGE INDEX: {0}", pageIndex);
        
        getQueryBuilder(fileUUID, pageIndex)
                .flatMap(qb -> getSearchSourceBuilder(qb));
        
        Result<SearchRequest> rSearchRequest = getQueryBuilder(fileUUID, pageIndex)
                .flatMap(qb -> getSearchSourceBuilder(qb))
                .flatMap(ssb -> getSearchRequest(groups,ssb));
        
        Result<SearchResponse> rSearchResponse = rSearchRequest
                .flatMap(sr -> search(sr));
        
        rSearchResponse
                .forEach(sr -> LOG.log(Level.INFO, "--> RESPONSE STATUS: {0}",
                        sr.status()));
        List<SearchHit> searchHits = rSearchResponse
                .filter(sr -> sr.status().equals(RestStatus.OK))
                .map(sr -> getSearchHits(sr))
                .getOrElse(Collections.EMPTY_LIST);
        
        return searchHits.stream().map(h -> toVirtualPage(h))
                .collect(Collectors.toList());
                
   }
    
    private List<SearchHit> getSearchHits(SearchResponse sr){
       SearchHits hits = sr.getHits();
       LOG.log(Level.INFO, "--> HITS SIZE: {0}", hits.totalHits);
       return Arrays.stream(hits.getHits()).collect(Collectors.toList());
   }
    
    private VirtualPage toVirtualPage(SearchHit hit){
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        VirtualPage vp = new VirtualPage();
        vp.setUuid((String)sourceAsMap.get(VirtualPageProperty.PAGE_UUID.name()));
        vp.setInodeUUID((String)sourceAsMap.get(VirtualPageProperty.FILE_UUID.name()));
        vp.setContent((String)sourceAsMap.get(VirtualPageProperty.CONTENT.name()));
        //vp.setPageIndex(Integer.valueOf((String)sourceAsMap.get(VirtualPageProperty.PAGE_INDEX.name())));
        LOG.log(Level.INFO, "--> VP PAGE UUID: {0}", sourceAsMap.get(VirtualPageProperty.PAGE_UUID.name()));
        LOG.log(Level.INFO, "--> VP PAGE INDEX: {0}", sourceAsMap.get(VirtualPageProperty.PAGE_INDEX.name()));
//        vp.setTotalPageCount(Integer.valueOf((String)sourceAsMap.get(VirtualPageProperty.TOTAL_PAGE_COUNT.name())));
        
        return vp;
    }
    
    private Result<SearchResponse> search(SearchRequest sr){
        try {
            return Result.success(acu.getRestHighLevelClient()
                    .search(sr, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            return Result.failure(ex);
        }
  }
    
   private Result<QueryBuilder> getQueryBuilder(String fileUUID,int pageIndex){
        var query = QueryBuilders.boolQuery()
               .must(QueryBuilders.termQuery("file_uuid", fileUUID))
               .must(QueryBuilders.termQuery("page_index", pageIndex));
        LOG.log(Level.INFO, "--> QUERY: {0}", query.toString());
        return Result.of(query);
   }
    
   private Result<SearchSourceBuilder> getSearchSourceBuilder(QueryBuilder qb){
       var ss = new SearchSourceBuilder();
       return Result.of(ss.query(qb));
   }
    
    private Result<SearchRequest> getSearchRequest(List<Group> groups,SearchSourceBuilder sb){
        String[] indices = groups.stream()
                .map(g -> elasticQueryUtils.getVirtualPageIndexName(g))
                .toArray(String[]::new);
        Arrays.stream(indices)
                .forEach(ind -> LOG.log(Level.INFO, "--|> INDEX: {0}", ind));
        var sr = new SearchRequest(indices, sb);
        return Result.of(sr);
    }
   
    private MultivaluedMap<String,Object> headers(){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "application/json");
        return headers;
    }
       
    private final Function<List<Group>,Result<String>> getMetadataUri = g ->  {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()
                + elasticQueryUtils.getMetadataIndicesString(g)
                + "/" + "metadatas" 
                + "/" + "_search";
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<List<Group>,Result<String>> getVirtualPagesUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()  
                + elasticQueryUtils.getVirtualPageIndicesString(g)
                + "/" + "virtual-pages" 
                + "/" + "_search";
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
}
