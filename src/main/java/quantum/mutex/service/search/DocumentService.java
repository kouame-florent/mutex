/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import com.google.gson.Gson;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.util.Constants;
import quantum.mutex.util.ServiceEndPoint;


/**
 *
 * @author Florent
 */
@Stateless
public class DocumentService {

    private static final Logger LOG = Logger.getLogger(DocumentService.class.getName());
   
    @Inject QueryUtils elasticApiUtils;
    @Inject ApiClientUtils apiClientUtils;
    
    public void indexMetadata(Group group,Metadata mdto){
        Result<String> json = toMatadataJson(mdto);
        Result<String> target = buildMetadataIndexingUri.apply(group).apply(mdto) ;
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
        resp.forEach(close);
    }
    
    public void indexVirtualPage(Group group,VirtualPage vpdto){
//        LOG.log(Level.INFO, "--> INDEX VP UUID: {0}", vpdto.getUuid());
        Result<String> json = toVirtualPageJson(vpdto);
        Result<String> target = buildVirtualPageIndexingUri.apply(group).apply(vpdto) ;
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
 
    private Result<String> toMatadataJson(Metadata mdto){
        
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("uuid", mdto.getUuid());
        jsonMap.put("file_uuid", mdto.getInodeUUID());
        jsonMap.put("file_name", mdto.getFileName());
        jsonMap.put("file_size", String.valueOf(mdto.getFileSize()));
        jsonMap.put("file_owner", mdto.getFileOwner());
        jsonMap.put("file_group", mdto.getFileGroup());
        jsonMap.put("file_tenant", mdto.getFileTenant());
        jsonMap.put("file_created", mdto.getFileCreated()
                .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)));
        jsonMap.put("attribute_name", mdto.getAttributeName());
        jsonMap.put("attribute_value", mdto.getAttributeValue());
        jsonMap.put("permissions", mdto.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
//        LOG.log(Level.INFO, "--> META JSON: {0}", jsonString);
        return Result.of(jsonString);
        
    }
    
    private Result<String> toVirtualPageJson(VirtualPage vpdto){
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("page_uuid", vpdto.getUuid());
        jsonMap.put("page_hash", vpdto.getPageHash());
        jsonMap.put("file_uuid", vpdto.getInodeUUID());
        jsonMap.put("file_name", vpdto.getFileName());
        jsonMap.put("content", vpdto.getContent());
        jsonMap.put("total_page_count", String.valueOf(vpdto.getTotalPageCount()));
        jsonMap.put("page_index", String.valueOf(vpdto.getPageIndex()));
        jsonMap.put("permissions", vpdto.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
//        LOG.log(Level.INFO, "--> VIRTUAL PAGE JSON: {0}", jsonString);
        return Result.of(jsonString);
    }
    
   
    private final Function<Group,Function<Metadata,Result<String>>> buildMetadataIndexingUri = g -> m -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()
                + elasticApiUtils.getMetadataIndexName(g) 
                + "/" + "metadatas" 
                + "/" + m.getHash();
//        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<Group,Function<VirtualPage,Result<String>>> buildVirtualPageIndexingUri = g -> v -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()  
                + elasticApiUtils.getVirtualPageIndexName(g)
                + "/" + "virtual-pages" 
                + "/" + v.getHash();
//        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Effect<Response> close = r -> {
        if(r != null) r.close();
    };
    
    
}
