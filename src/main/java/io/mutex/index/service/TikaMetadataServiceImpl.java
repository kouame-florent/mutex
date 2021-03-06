/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;


import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import io.mutex.search.valueobject.FileInfo;
import io.mutex.search.valueobject.Metadata;
import io.mutex.index.entity.Inode;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.user.service.SpaceService;



/**
 *
 * @author Florent
 */
@Stateless
public class TikaMetadataServiceImpl implements TikaMetadataService {
   
    private static final Logger LOG = Logger.getLogger(TikaMetadataServiceImpl.class.getName());
    
    @Inject TikaServerService tikaServerService;
    @Inject EnvironmentUtils envUtils;
    @Inject SpaceService spaceService;
   
    @Override
    public Map<String,String> getMetadatas( Path filePath){
        
        Optional<InputStream> ins = openInputStream(filePath);
        Map<String,String> metas = ins.flatMap(in -> tikaServerService.getMetas(in))
                .flatMap(res -> toJson(res))
                .map(json -> unmarshallToMap(json))
                .orElseGet(() -> Collections.EMPTY_MAP);
        ins.ifPresent(i -> closeInputStream(i));

        return metas;
     }
    
    private Optional<String> toJson (Response response){
       return Optional.of(response.readEntity(String.class)) ;
    }
    
    private Map<String,String> unmarshallToMap(String jsonString){
        Gson gson = new Gson();
        Map<String,String> res = gson.fromJson(jsonString, Map.class);
        LOG.log(Level.INFO, "-- JSON MAP: {0}", res);
        return res;
    }
    
    @Override
    public Metadata buildMutexMetadata(FileInfo fileInfo,Inode inode,Map<String,String> map){
       LOG.log(Level.INFO, "---->>-->> -- INODE DATE TIME: {0}", inode.getCreated());
       Metadata meta = new Metadata();
       meta.setFileName(fileInfo.getFileName());
       meta.setFileSize(fileInfo.getFileSize());
       meta.setFileCreated(inode.getCreated().toEpochSecond(ZoneOffset.UTC));
       getContentType(map).ifPresent(c -> meta.setFileMimeType(c));
       meta.setFileGroup(fileInfo.getFileGroup().getName());
       meta.setFileOwner(envUtils.getUserlogin());
       meta.setFileSpace(fileInfo.getFileGroup().getSpace().getName());
       meta.setContent("file_name: ".concat(fileInfo.getFileName()).concat("; ")
               .concat(getMetadatasAsString(map)));
       meta.setInodeHash(inode.getFileHash());
       meta.setInodeUUID(inode.getUuid());
       meta.setGroupUUID(fileInfo.getFileGroup().getUuid());
      
       return meta;
    }
    
   
    private Optional<InputStream> openInputStream(Path filePath){
        try {
             return Optional.ofNullable(Files.newInputStream(filePath));
        } catch (IOException ex) {
            Logger.getLogger(TikaMetadataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
      
    private void closeInputStream(InputStream in){
        try{
            if(in != null) in.close();
        }catch(IOException ex){
            Logger.getLogger(TikaMetadataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Optional<String> getLanguage(Map<String,String> map){
        String res = map.get("language");
        return res != null ? Optional.of(res) : Optional.of("fr");
    }
    
    @Override
    public Optional<String> getContentType(Map<String,String> map){
        String res = map.get("Content-Type");
        return res != null ? Optional.of(res) : Optional.of("application/octet-stream");
    }
    
    @Override
    public String getMetadatasAsString(Map<String,String> map){
        
        return map.entrySet().stream().filter(e -> !e.getKey().equals("X-Parsed-By"))
                .map(e -> e.getKey() + ": " + String.valueOf(e.getValue()) )
                .collect(Collectors.joining("; "));
    } 

}
