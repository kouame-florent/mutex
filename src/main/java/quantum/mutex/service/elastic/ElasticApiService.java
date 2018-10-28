/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.elastic;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import quantum.mutex.common.Function;
import quantum.mutex.common.Nothing;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.service.config.MappingConfigLoader;

/**
 *
 * @author Florent
 */
@Stateless
public class ElasticApiService {

    private static final Logger LOG = Logger.getLogger(ElasticApiService.class.getName());
       
    @Inject MappingConfigLoader mappingConfigLoader;
    @Inject ApiClientUtils apiClientUtils;
    
    public void createIndex(Group group){
        Result<String> json =  mappingConfigLoader.retrieveVirtualPageMapping();
        Result<String> target = buildMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j))));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
    }
    
    private final Function<Group,Result<String>> buildMappingUri = g -> {
        String target = "http://localhost:9200/" + g.getName();
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
           
    
    
}
