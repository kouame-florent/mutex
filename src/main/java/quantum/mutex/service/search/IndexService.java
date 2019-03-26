/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.config.ElasticMappingConfigLoader;
import quantum.mutex.util.ServiceEndPoint;



/**
 *
 * @author Florent
 */
@Stateless
public class IndexService {

    private static final Logger LOG = Logger.getLogger(IndexService.class.getName());
       
    @Inject ElasticMappingConfigLoader mappingConfigLoader;
    @Inject ApiClientUtils apiClientUtils;
    @Inject QueryUtils elasticApiUtils;
    
//    public void createsIndices(Group group){
//        mappingMetadata(group);
//        mappingVirtualPage(group);
//    }
    
//    public void createMetadataIndex(Group group){
//        mappingMetadata(group);
//      
//    }
//    
//    public void createVirtualPageIndex(Group group){
//        mappingVirtualPage(group);
//    }
   
   
    public void createMetadataIndex(Group group){
        
        Result<String> json =  mappingConfigLoader.retrieveMetadataMapping();
        Result<String> target = buildMetadataMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
        resp.forEach(close);
        
    }
    
    private MultivaluedMap<String,Object> headers(){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "application/json");
        return headers;
    }
    
    public void createVirtualPageIndex(Group group){
        Result<String> json =  mappingConfigLoader.retrieveVirtualPageMapping();
        Result<String> target = buildVirtualPageMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
        resp.forEach(close);
   }
    
    private final Function<Group,Result<String>> buildMetadataMappingUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()
                + elasticApiUtils.getMetadataIndexName(g);
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<Group,Result<String>> buildVirtualPageMappingUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value() 
                + elasticApiUtils.getVirtualPageIndexName(g);
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Effect<Response> close = r -> {
        if(r != null) r.close();
    };
    
    
   
}
