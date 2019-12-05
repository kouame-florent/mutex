/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import mutex.search.valueobject.Fragment;



/**
 *
 * @author Florent
 */
@Stateless
public class ElasticResponseHandler {

    private static final Logger LOG = Logger.getLogger(ElasticResponseHandler.class.getName());
    
  
    public Optional<JsonObject> marshall(String json){
        Gson gson = new Gson();
        return Optional.of(gson.fromJson(json, JsonObject.class));
    }
    
  
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
       
//        String fileUUID = getInodeUUID(jsonElement);
//        String pageUUID = getPageUUID(jsonElement);
//        String fileName = getFileName(jsonElement);
//        int pageIndex = getPageIndex(jsonElement);
//        int totalPageCount = getTotalPageCount(jsonElement);
//        JsonObject highLight = jsonElement.getAsJsonObject().getAsJsonObject("highlight");
//        highLight.getAsJsonArray("content")
//                .forEach(c -> highlights.add(new Fragment(pageUUID,fileUUID,fileName,pageIndex,
//                        totalPageCount, c.getAsString())));
        highlights.add(newFragment(jsonElement));
        return highlights;
    }
    
    private Fragment newFragment(JsonElement jsonElement){
        String inodeUUID = getInodeUUID(jsonElement);
        String pageUUID = getPageUUID(jsonElement);
        String fileName = getFileName(jsonElement);
        int pageIndex = getPageIndex(jsonElement);
        int totalPageCount = getTotalPageCount(jsonElement);
        JsonObject highLight = jsonElement.getAsJsonObject().getAsJsonObject("highlight");
        String content = highLight.getAsJsonArray("content").getAsString();
        
        return new Fragment.Builder()
                    .pageUUID(pageUUID)
                    .inodeUUID(inodeUUID)
                    .fileName(fileName)
                    .pageIndex(pageIndex)
                    .totalPageCount(totalPageCount)
                    .content(content).build();
    }
    
    private int getPageIndex(JsonElement jsonElement){
        return  jsonElement.getAsJsonObject().getAsJsonObject("_source")
                    .get("page_index").getAsInt();
    }
    
    private int getTotalPageCount(JsonElement jsonElement){
        return  jsonElement.getAsJsonObject().getAsJsonObject("_source")
                    .get("total_page_count").getAsInt();
    }
        
    private String getInodeUUID(JsonElement jsonElement){
        return  jsonElement.getAsJsonObject().getAsJsonObject("_source")
                    .get("inode_uuid").getAsString();
    }
    
    private String getFileName(JsonElement jsonElement){
        return  jsonElement.getAsJsonObject().getAsJsonObject("_source")
                    .get("file_name").getAsString();
    }
    
    private String getPageUUID(JsonElement jsonElement){
        return  jsonElement.getAsJsonObject().getAsJsonObject("_source")
                    .get("page_uuid").getAsString();
    }
 
}
