/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.Fragment;
import quantum.mutex.domain.dto.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class ElasticResponseHandler {

    private static final Logger LOG = Logger.getLogger(ElasticResponseHandler.class.getName());
    
  
    public Result<JsonObject> marshall(String json){
        Gson gson = new Gson();
        return Result.of(gson.fromJson(json, JsonObject.class));
    }
    
//    public List<VirtualPage> getPages(JsonObject jsonObject){
//        List<JsonElement> jsonElements = new ArrayList<>();
//        jsonObject.getAsJsonObject("hits").get("hits")
//                .getAsJsonArray().forEach(je -> jsonElements.add(je));
//        
//        return jsonElements.stream().map(this::getSource)
//                    .map(this::buildPage).collect(Collectors.toList());
//    }
    
//    private JsonObject getSource(JsonElement jsonElement){
//        return jsonElement
//                .getAsJsonObject()
//                    .getAsJsonObject("_source");
//    }
//    
    public Set<Fragment> getFragments(JsonObject jsonObject){
        List<JsonElement> jsonElements = new ArrayList<>();
        jsonObject.getAsJsonObject("hits").get("hits")
                .getAsJsonArray().forEach(je -> jsonElements.add(je));
        
        return jsonElements.stream()
                    .map(this::buildHighLights).flatMap(List::stream)
                    .collect(Collectors.toSet());
    }
    
   
    
    private List<Fragment> buildHighLights(JsonElement jsonElement){
        
        List<Fragment> highlights = new ArrayList<>();
       
        String fileUUID = getFileUUID(jsonElement);
        String uuid = getUUID(jsonElement);
        JsonObject highLight = jsonElement.getAsJsonObject().getAsJsonObject("highlight");
        highLight.getAsJsonArray("content")
                .forEach(c -> highlights.add(new Fragment(uuid,fileUUID, c.getAsString())));
            
        return highlights;
    }
    
    private String getFileUUID(JsonElement jsonElement){
        return  jsonElement.getAsJsonObject().getAsJsonObject("_source")
                    .get("file_uuid").getAsString();
    }
    
    private String getUUID(JsonElement jsonElement){
        return  jsonElement.getAsJsonObject().getAsJsonObject("_source")
                    .get("page_uuid").getAsString();
    }
    
//   
//    
//    private JsonObject getHighlightsElement(JsonElement jsonElement){
//        return jsonElement.getAsJsonObject().getAsJsonObject("highlight");
//    }
//    
//    
    
//    private VirtualPage buildPage(JsonObject jsonObject){
//        VirtualPage pageDTO = new VirtualPage();
//        pageDTO.setUuid(jsonObject.get("uuid").getAsString());
//        pageDTO.setMutexFileUUID(jsonObject.get("file_uuid").getAsString());
//        pageDTO.setPageIndex(jsonObject.get("page_index").getAsInt());
//        pageDTO.setContent(jsonObject.get("content").getAsString());
//        return pageDTO;
//    }
    
}
