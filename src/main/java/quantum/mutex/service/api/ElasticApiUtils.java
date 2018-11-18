/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;

import com.google.gson.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.util.EnvironmentUtils;

/**
 *
 * @author Florent
 */
@Stateless
public class ElasticApiUtils {

    private static final Logger LOG = Logger.getLogger(ElasticApiUtils.class.getName());
    
    @Inject EnvironmentUtils envUtils;
    
    public String getMetadataIndexName(@NotNull Group group){
        return envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
                + "$" 
                + group.getName().replaceAll(" ", "_").toLowerCase()
                + "$" + 
                "metadata";
    }
    
    public String getVirtualPageIndexName(@NotNull Group group){
        return envUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
                + "$" 
                + group.getName().replaceAll(" ", "_").toLowerCase()
                + "$" + 
                "virtual_page";
    }
    
    public Result<JsonObject> termQuery(String term){
        JsonObject jsonObject = new JsonObject();
        
        return Result.of(new JsonObject());
    }
      
    public Result<JsonObject> matchQuery(String text){
        
        JsonObject root = new JsonObject();
        JsonObject query = new JsonObject();
        JsonObject match = new JsonObject();
         
        match.addProperty("content", text);
        query.add("match", match);
        root.add("query", query);
        
        LOG.log(Level.INFO, "---> JSON QUERY: {0}", root.toString());
        
        return Result.of(root);
    }
    
}
