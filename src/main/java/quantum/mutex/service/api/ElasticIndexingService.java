/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.dto.VirtualPage;


/**
 *
 * @author Florent
 */
@Stateless
public class ElasticIndexingService {

    private static final Logger LOG = Logger.getLogger(ElasticIndexingService.class.getName());
   
    @Inject ElasticQueryUtils elasticApiUtils;
    @Inject ApiClientUtils apiClientUtils;
    
    public final static String ELASTIC_SEARCH_SERVER_URI = "http://localhost:9200/";
    
    public void indexingMetadata(Group group,Metadata mdto){
        Result<String> json = toMatadataJson(mdto);
        Result<String> target = buildMetadataIndexingUri.apply(group).apply(mdto) ;
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
    }
    
   
    
     public void indexingVirtualPage(Group group,VirtualPage vpdto){
        Result<String> json = toVirtualPageJson(vpdto);
        Result<String> target = buildVirtualPageIndexingUri.apply(group).apply(vpdto) ;
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
    }
     
    private MultivaluedMap<String,Object> headers(){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "application/json");
        return headers;
    }
 
    private Result<String> toMatadataJson(Metadata mdto){
        
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("uuid", mdto.getUuid());
        jsonMap.put("file_uuid", mdto.getMutexFileUUID());
        jsonMap.put("attribute_name", mdto.getAttributeName());
        jsonMap.put("attribute_value", mdto.getAttributeValue());
        jsonMap.put("permissions", mdto.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
        LOG.log(Level.INFO, "--> META JSON: {0}", jsonString);
        return Result.of(jsonString);
        
    }
    
    private Result<String> toVirtualPageJson(VirtualPage vpdto){
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("uuid", vpdto.getUuid());
        jsonMap.put("page_hash", vpdto.getPageHash());
        jsonMap.put("file_uuid", vpdto.getMutexFileUUID());
        jsonMap.put("content", vpdto.getContent());
        jsonMap.put("page_index", String.valueOf(vpdto.getPageIndex()));
        jsonMap.put("permissions", vpdto.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
        LOG.log(Level.INFO, "--> META JSON: {0}", jsonString);
        return Result.of(jsonString);
    }
    
   
    private final Function<Group,Function<Metadata,Result<String>>> buildMetadataIndexingUri = g -> m -> {
        String target = ELASTIC_SEARCH_SERVER_URI 
                + elasticApiUtils.getMetadataIndexName(g) 
                + "/" + "metadatas" 
                + "/" + m.getUuid();
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<Group,Function<VirtualPage,Result<String>>> buildVirtualPageIndexingUri = g -> v -> {
        String target = ELASTIC_SEARCH_SERVER_URI  
                + elasticApiUtils.getVirtualPageIndexName(g)
                + "/" + "virtual-pages" 
                + "/" + v.getUuid();
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
}
