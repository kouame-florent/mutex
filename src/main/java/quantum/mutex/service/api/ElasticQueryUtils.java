/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.EnvironmentUtils;

/**
 *
 * @author Florent
 */
@Stateless
public class ElasticQueryUtils {

    private static final Logger LOG = Logger.getLogger(ElasticQueryUtils.class.getName());
    
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
         
        LOG.log(Level.INFO, "---> MATCH JSON QUERY: {0}", root.toString());
        
        return Result.of(root);
    }
    
    public Result<JsonObject> matchPhraseQuery(String text){
        JsonObject root = new JsonObject();
        JsonObject query = new JsonObject();
        JsonObject matchPhrase = new JsonObject();
        JsonObject content = new JsonObject();
        
        content.addProperty("query", text);
//        content.addProperty("slop", 1);
        
        matchPhrase.add("content", content);
        
        query.add("match_phrase", matchPhrase);
        
        root.add("query", query);
         
        LOG.log(Level.INFO, "---> PHRASE JSON QUERY: {0}", root.toString());
        
        return Result.of(root);
    }
    
    public Result<JsonObject> addHighlighting(JsonObject rootObject){
        JsonObject highlight = new JsonObject();
        JsonObject highlightFields = new JsonObject();
        JsonArray preTags = new JsonArray();
        JsonArray postTags = new JsonArray();
        
        preTags.add("<b style='color: #32a851'>");
        postTags.add("</b>");
        
        highlightFields.add("content", new JsonObject());
        highlight.add("fields", highlightFields);
        highlight.add("pre_tags", preTags);
        highlight.add("post_tags", postTags);
        highlight.addProperty("fragment_size", 400);
        highlight.addProperty("order", "score");
        
        rootObject.add("highlight", highlight);
        LOG.log(Level.INFO, "---> AFTER HIGHLIGHT JSON QUERY: {0}", rootObject.toString());
        return Result.of(rootObject);
    }
    
     public Function<JsonObject, Function<Integer,Result<JsonObject>>> addSize = j -> s -> {
         return Result.empty();
     };
    
}
