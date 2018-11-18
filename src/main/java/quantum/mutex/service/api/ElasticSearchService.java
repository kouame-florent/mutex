/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;


import com.google.gson.JsonObject;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import quantum.functional.api.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.util.EnvironmentUtils;


/**
 *
 * @author Florent
 */
@Stateless
public class ElasticSearchService {

    private static final Logger LOG = Logger.getLogger(ElasticSearchService.class.getName());
     
    
    @Inject ApiClientUtils acu;
    @Inject ElasticApiUtils elasticApiUtils;
   
    
    public final static String ELASTIC_SEARCH_SERVER_URI = "http://localhost:9200/";
    
    public Result<String> search(Group group,String text){
        Result<String> json = elasticApiUtils.matchQuery(text).map(jo -> jo.toString());
        return getVirtualPagesUri.apply(group)
                    .flatMap(uri -> json.flatMap(js -> acu.post(uri, Entity.json(js),headers())))
                    .map(r -> r.readEntity(String.class));
    }
    
    private MultivaluedMap<String,Object> headers(){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "application/json");
        return headers;
    }
    
    
    private final Function<Group,Result<String>> getMetadataUri = g ->  {
        String target = ELASTIC_SEARCH_SERVER_URI 
                + elasticApiUtils.getMetadataIndexName(g) 
                + "/" + "metadatas" 
                + "/" + "_search";
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<Group,Result<String>> getVirtualPagesUri = g -> {
        String target = ELASTIC_SEARCH_SERVER_URI  
                + elasticApiUtils.getVirtualPageIndexName(g)
                + "/" + "virtual-pages" 
                + "/" + "_search";
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
}
