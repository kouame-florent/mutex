/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.elastic;


import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.dto.MetadataDTO;


/**
 *
 * @author Florent
 */
@Stateless
public class IndexingService {

    private static final Logger LOG = Logger.getLogger(IndexingService.class.getName());
   
    @Inject ElasticApiUtils elasticApiUtils;
    @Inject ApiClientUtils apiClientUtils;
    
    public void indexingMetadata(Group group,MetadataDTO mdto){
        Result<String> json = buildMatadataJson(mdto);
        Result<String> target = buildMetadataIndexingUri.apply(group).apply(mdto) ;
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j))));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
    }
    
//    private final Function<MetadataDTO,Result<String>> buildMatadataJson = m -> {
//        Gson gson = new Gson();
//        String json = gson.toJson(m);
//        LOG.log(Level.INFO, "--> META JSON: {0}", json);
//        return Result.of(json);
//    };
    
    private Result<String> buildMatadataJson(MetadataDTO mdto){
        
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
    
    private final Function<Group,Function<MetadataDTO,Result<String>>> buildMetadataIndexingUri = g -> m -> {
        String target = "http://localhost:9200/" 
                + elasticApiUtils.getMetadataIndexName(g) 
                + "/" + "metadatas" 
                + "/" + m.getUuid();
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
}
