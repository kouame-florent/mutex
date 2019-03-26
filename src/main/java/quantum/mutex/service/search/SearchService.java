/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;



import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.elasticsearch.search.builder.SearchSourceBuilder;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.User;
import quantum.mutex.util.IndexName;
import quantum.mutex.util.ServiceEndPoint;



/**
 *
 * @author Florent
 */
@Stateless
public class SearchService {

    private static final Logger LOG = Logger.getLogger(SearchService.class.getName());
    
    @Inject ApiClientUtils acu;
    @Inject QueryUtils elasticQueryUtils;
     
//    public final static String ELASTIC_SEARCH_SERVER_URI = "http://localhost:9200/";
    
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
    
    public void searchForTermQuery(String pageUUID,int pageIndex){
//       SearchRequest searchRequest = new SearchRequest(IndexName.VIRTUAL_PAGE.name()); 
//       SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//       SearchSourceBuilder sb = searchSourceBuilder.query(QueryBuilders.boolQuery()
//               .must(QueryBuilders.termQuery("file_uuid", pageUUID))
//               .must(QueryBuilders.termQuery("page_index", pageIndex)));
//       String result = sb.toString();
//       LOG.log(Level.INFO, "--> QUERY STRING: {0}", result);
//       searchRequest.source(searchSourceBuilder);

        Result<SearchRequest> rSearchRequest = getQueryBuilder(pageUUID, pageIndex)
                .flatMap(qb -> getSearchSourceBuilder(qb))
                .flatMap(ssb -> getSearchRequest(ssb));
        
        Result<SearchResponse> rSearchResponse = rSearchRequest
                .flatMap(sr -> search(sr));
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
        return Result.of(query);
   }
    
   private Result<SearchSourceBuilder> getSearchSourceBuilder(QueryBuilder qb){
       var ss = new SearchSourceBuilder();
       return Result.of(ss.query(qb));
   }
    
   private Result<SearchRequest> getSearchRequest(SearchSourceBuilder sb){
       var sr = new SearchRequest(IndexName.VIRTUAL_PAGE.name());
       return Result.of(sr.source(sb));
   }
    
   
   
    private MultivaluedMap<String,Object> headers(){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "application/json");
        return headers;
    }
   
    private final Function<List<Group>,Result<String>> getMetadataUri = g ->  {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()
                + elasticQueryUtils.getMetadataIndices(g)
                + "/" + "metadatas" 
                + "/" + "_search";
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<List<Group>,Result<String>> getVirtualPagesUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()  
                + elasticQueryUtils.getVirtualPageIndices(g)
                + "/" + "virtual-pages" 
                + "/" + "_search";
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
}
