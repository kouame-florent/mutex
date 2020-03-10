/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import java.io.InputStream;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import io.mutex.index.valueobject.ServiceEndPoint;
import java.util.Map;


/**
 *
 * @author Florent
 */
public class TikaServerServiceImpl implements TikaServerService {
    
    @Inject RestClientUtilImpl apiClientUtils;
    @Inject TikaMetadataServiceImpl tikaMetadataService;
   
    private Optional<String> buildMetaResourceUri(){
        return Optional.of(ServiceEndPoint.TIKA_BASE_URI.value() + TikaResourceURI.META.uri());
    }
        
    private MultivaluedMap<String,Object> getHeaders(String accept){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", accept);
        
        return headers;
    }
    
    private MultivaluedMap<String,Object> getHeaders(String accept,Map<String,String> metas){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", accept);
        headers.add("Content-type", tikaMetadataService.getContentType(metas));
        headers.add("X-Tika-OCRLanguage", "eng");
        
        return headers;
    }
    
    @Override
    public Optional<Response> getMetas(InputStream inputStream){
        Optional<Entity> resEn = buildRawEntity(inputStream);
        Optional<String> resUri = buildMetaResourceUri();
        return resEn.flatMap(e -> resUri.flatMap(uri -> apiClientUtils.put(uri, e,getHeaders("application/json"))));
    }
    
    @Override
    public Optional<Response> getContent(InputStream inputStream,Map<String,String> metas){
        Optional<Entity> oEnTity = buildRawEntity(inputStream);
        Optional<String> oResource = buildContentResourceUri();
        return oEnTity.flatMap(e -> oResource.flatMap(uri -> apiClientUtils.put(uri, 
                e,getHeaders("text/plain",metas))));
    }
       
    private Optional<Entity> buildRawEntity(InputStream inputStream){
       return Optional.of(Entity.entity(inputStream, MediaType.APPLICATION_OCTET_STREAM_TYPE));
    }
    
    private Optional<String> buildContentResourceUri(){
        return Optional.of(ServiceEndPoint.TIKA_BASE_URI.value() + TikaResourceURI.TIKA.uri());
    }
    
}
