/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.elastic;


import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import quantum.functional.api.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.service.config.MappingConfigLoader;



/**
 *
 * @author Florent
 */
@Stateless
public class MappingService {

    private static final Logger LOG = Logger.getLogger(MappingService.class.getName());
       
    @Inject MappingConfigLoader mappingConfigLoader;
    @Inject ApiClientUtils apiClientUtils;
    @Inject ElasticApiUtils elasticApiUtils;
    
    public void createsIndices(Group group){
        mappingMetadata(group);
        mappingVirtualPage(group);
    }
   
    private void mappingMetadata(Group group){
        Result<String> json =  mappingConfigLoader.retrieveMetadataMapping();
        Result<String> target = buildMetadataMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j))));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
    }
    
    private void mappingVirtualPage(Group group){
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
    
   
}
