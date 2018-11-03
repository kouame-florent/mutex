/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.elastic;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import quantum.mutex.common.Effect;
import quantum.mutex.common.Function;
import quantum.mutex.common.Nothing;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.dto.MetadataDTO;
import quantum.mutex.service.config.MappingConfigLoader;
import quantum.mutex.service.index.MetadataIndexService;


/**
 *
 * @author Florent
 */
@Stateless
public class ElasticApiService {

    private static final Logger LOG = Logger.getLogger(ElasticApiService.class.getName());
       
    @Inject MappingConfigLoader mappingConfigLoader;
    @Inject ApiClientUtils apiClientUtils;
    @Inject ElasticApiUtils elasticApiUtils;
    @Inject MetadataIndexService mis;
    
   
    public void createsIndices(Group group){
        createMetadataIndex(group);
        createVirtualPageIndex(group);
    }
    
    public void indexMetadata(Group group,MetadataDTO metadataDTO){
       Result<IndexRequest> indexRequest =  buildMetadataIndexRequest.apply(group).apply(metadataDTO)
                .flatMap(i -> provideSource.apply(i)
                        .apply(mis.getJsonMap(metadataDTO)));
       
       Result<RestHighLevelClient> client = initClient.apply(Nothing.instance);
       client.flatMap(c -> indexRequest.map(i -> doIndex.apply(c).apply(i)));
       
      client.forEach(close);
               
    }
     
   
    private void createMetadataIndex(Group group){
        Result<String> json =  mappingConfigLoader.retrieveMetadataMapping();
        Result<String> target = buildMetadataMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j))));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
    }
    
    private void createVirtualPageIndex(Group group){
        Result<String> json =  mappingConfigLoader.retrieveVirtualPageMapping();
        Result<String> target = buildVirtualPageMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j))));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
    }
    
    private final Function<Group,Result<String>> buildMetadataMappingUri = g -> {
        String target = "http://localhost:9200/" 
                + elasticApiUtils.getMetadataIndexName(g);
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<Group,Result<String>> buildVirtualPageMappingUri = g -> {
        String target = "http://localhost:9200/" 
                + elasticApiUtils.getVirtualPageIndexName(g);
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
   
    private final Function<Group,Function<MetadataDTO,Result<IndexRequest>>> 
            buildMetadataIndexRequest = g -> m ->{
                
        IndexRequest indexRequest = new IndexRequest(elasticApiUtils.getMetadataIndexName(g), 
                "metadatas", m.getUuid());
        return Result.of(indexRequest);
    };
    
    private final Function<IndexRequest,Function<Map,Result<IndexRequest>>>
             provideSource = i -> m -> {
             return Result.of(i.source(m));
    };
    
    private final Function<Nothing,Result<RestHighLevelClient>> initClient = n ->{
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));
        return Result.of(client);
    };
    
    private final Function<RestHighLevelClient,Function<IndexRequest,IndexRequest>> 
            doIndex = c -> i -> {
                try{
                    c.index(i, RequestOptions.DEFAULT);
                }catch(IOException ex){
                    ex.printStackTrace();
                }
        return i;
    };
    
    private final Effect<RestHighLevelClient> close = c -> {
        if(c != null){
            try{
                c.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    };
    
    
}
