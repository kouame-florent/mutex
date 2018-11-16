/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;

import java.io.InputStream;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import quantum.functional.api.Result;

/**
 *
 * @author Florent
 */
public class TikaServerService {
    
    @Inject ApiClientUtils apiClientUtils;
    
    public final static String TIKA_SERVER_URI = "http://localhost:9999/";
    
    private Result<String> buildMetaResourceUri(){
        return Result.of(TIKA_SERVER_URI + TikaResourceURI.META.uri());
    }
    
    private Result<String> buildLangResourceUri(){
        return Result.of(TIKA_SERVER_URI + TikaResourceURI.LANGUAGE.uri());
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
    
//    public Result<Response> getLanguage(InputStream inputStream){
//        Result<Entity> resEn = buildRawEntity(inputStream);
//        Result<String> resUri = buildLangResourceUri();
//        return resEn.flatMap(e -> resUri.flatMap(uri -> apiClientUtils.put(uri, e,headers("text/plain"))));
//    }
    
}
