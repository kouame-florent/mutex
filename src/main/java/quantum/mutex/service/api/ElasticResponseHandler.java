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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import quantum.functional.api.Result;
import quantum.mutex.dto.VirtualPageDTO;

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
    
    public List<VirtualPageDTO> getPages(JsonObject jsonObject){
        List<JsonElement> jsonElements = new ArrayList<>();
        jsonObject.getAsJsonObject("hits").get("hits")
                .getAsJsonArray().forEach(je -> jsonElements.add(je));
        
        return jsonElements.stream().map(this::getSource)
                    .map(this::fromVirtualPageJason).collect(Collectors.toList());
    }
    
    private JsonObject getSource(JsonElement jsonElement){
        return jsonElement
                .getAsJsonObject()
                    .getAsJsonObject("_source");
    }
    
    private VirtualPageDTO fromVirtualPageJason(JsonObject jsonObject){
        VirtualPageDTO pageDTO = new VirtualPageDTO();
        pageDTO.setUuid(jsonObject.get("uuid").getAsString());
        pageDTO.setMutexFileUUID(jsonObject.get("file_uuid").getAsString());
        pageDTO.setPageIndex(jsonObject.get("page_index").getAsInt());
        return pageDTO;
    }
    
}
