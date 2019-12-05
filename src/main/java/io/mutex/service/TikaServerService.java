/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service;

import mutex.util.RestClientUtil;
import java.io.InputStream;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import mutex.util.ServiceEndPoint;


/**
 *
 * @author Florent
 */
public class TikaServerService {
    
    @Inject RestClientUtil apiClientUtils;
   
    private Optional<String> buildMetaResourceUri(){
        return Optional.of(ServiceEndPoint.TIKA_BASE_URI.value() + TikaResourceURI.META.uri());
    }
    
    private Optional<String> buildContentResourceUri(){
        return Optional.of(ServiceEndPoint.TIKA_BASE_URI.value() + TikaResourceURI.TIKA.uri());
    }
    
    private Optional<String> buildLangResourceUri(){
        return Optional.of(ServiceEndPoint.TIKA_BASE_URI.value() + TikaResourceURI.LANGUAGE.uri());
    }
    
    private Optional<Entity> buildRawEntity(InputStream inputStream){
       return Optional.of(Entity.entity(inputStream, MediaType.APPLICATION_OCTET_STREAM_TYPE));
    }
    
    private MultivaluedMap<String,Object> headers(String accept){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", accept);
        return headers;
    }
    
    public Optional<Response> getMetas(InputStream inputStream){
        Optional<Entity> resEn = buildRawEntity(inputStream);
        Optional<String> resUri = buildMetaResourceUri();
        return resEn.flatMap(e -> resUri.flatMap(uri -> apiClientUtils.put(uri, e,headers("application/json"))));
    }
    
    public Optional<Response> getContent(InputStream inputStream){
        Optional<Entity> resEn = buildRawEntity(inputStream);
        Optional<String> resUri = buildContentResourceUri();
        return resEn.flatMap(e -> resUri.flatMap(uri -> apiClientUtils.put(uri, e,headers("text/plain"))));
    }
    
    
}
