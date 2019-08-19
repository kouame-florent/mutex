/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import quantum.mutex.util.RestClientUtil;
import java.io.InputStream;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import quantum.mutex.util.ServiceEndPoint;
import quantum.mutex.util.functional.Result;

/**
 *
 * @author Florent
 */
public class TikaServerService {
    
    @Inject RestClientUtil apiClientUtils;
   
    private Result<String> buildMetaResourceUri(){
        return Result.of(ServiceEndPoint.TIKA_BASE_URI.value() + TikaResourceURI.META.uri());
    }
    
    private Result<String> buildContentResourceUri(){
        return Result.of(ServiceEndPoint.TIKA_BASE_URI.value() + TikaResourceURI.TIKA.uri());
    }
    
    private Result<String> buildLangResourceUri(){
        return Result.of(ServiceEndPoint.TIKA_BASE_URI.value() + TikaResourceURI.LANGUAGE.uri());
    }
    
    private Result<Entity> buildRawEntity(InputStream inputStream){
       return Result.of(Entity.entity(inputStream, MediaType.APPLICATION_OCTET_STREAM_TYPE));
    }
    
    private MultivaluedMap<String,Object> headers(String accept){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", accept);
        return headers;
    }
    
    public Result<Response> getMetas(InputStream inputStream){
        Result<Entity> resEn = buildRawEntity(inputStream);
        Result<String> resUri = buildMetaResourceUri();
        return resEn.flatMap(e -> resUri.flatMap(uri -> apiClientUtils.put(uri, e,headers("application/json"))));
    }
    
    public Result<Response> getContent(InputStream inputStream){
        Result<Entity> resEn = buildRawEntity(inputStream);
        Result<String> resUri = buildContentResourceUri();
        return resEn.flatMap(e -> resUri.flatMap(uri -> apiClientUtils.put(uri, e,headers("text/plain"))));
    }
    
    
}
