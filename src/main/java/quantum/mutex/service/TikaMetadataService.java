/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import quantum.mutex.domain.type.FileInfo;
import quantum.mutex.domain.type.Metadata;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.service.search.TikaServerService;
import quantum.mutex.util.EnvironmentUtils;
import quantum.mutex.util.functional.Result;


/**
 *
 * @author Florent
 */
@Stateless
public class TikaMetadataService {
   
    private static final Logger LOG = Logger.getLogger(TikaMetadataService.class.getName());
    
    @Inject TikaServerService tss;
    @Inject EnvironmentUtils envUtils;
   
    public Map<String,String> getMetadata( Path filePath){
        
        Result<InputStream> ins = openInputStream(filePath);
        Map<String,String> metas = ins.flatMap(in -> tss.getMetas(in))
                .flatMap(res -> toJson(res))
                .map(json -> unmarshallToMap(json))
                .getOrElse(() -> Collections.EMPTY_MAP);
        ins.forEach(i -> closeInputStream(i));

        return metas;
     }
    
    private Result<String> toJson (Response response){
       return Result.of(response.readEntity(String.class)) ;
    }
    
    private Map<String,String> unmarshallToMap(String jsonString){
        Gson gson = new Gson();
        Map<String,String> res = gson.fromJson(jsonString, Map.class);
        LOG.log(Level.INFO, "-- JSON MAP: {0}", res);
        return res;
    }
    
    public Metadata buildMutexMetadata(FileInfo fileInfo,Inode inode,Map<String,String> map){
       LOG.log(Level.INFO, "---->>-->> -- INODE DATE TIME: {0}", inode.getCreated());
       Metadata meta = new Metadata();
       meta.setFileName(fileInfo.getFileName());
       meta.setFileSize(fileInfo.getFileSize());
       meta.setFileCreated(inode.getCreated().toEpochSecond(ZoneOffset.UTC));
       getContentType(map).forEach(c -> meta.setFileMimeType(c));
       meta.setFileGroup(fileInfo.getFileGroup().getName());
       meta.setFileOwner(envUtils.getUserlogin());
       meta.setFileTenant(envUtils.getUserTenantName());
       meta.setContent("file_name: ".concat(fileInfo.getFileName()).concat("; ")
               .concat(getMetadatasAsString(map)));
       meta.setInodeHash(inode.getFileHash());
       meta.setInodeUUID(inode.getUuid().toString());
      
       return meta;
    }

    private Result<InputStream> openInputStream(Path filePath){
        try {
             return Result.success(Files.newInputStream(filePath));
        } catch (IOException ex) {
            Logger.getLogger(TikaMetadataService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
    }
      
    private void closeInputStream(InputStream in){
        try{
            if(in != null) in.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public Result<String> getLanguage(Map<String,String> map){
        String res = map.get("language");
        return res != null ? Result.of(res) : Result.of("fr");
    }
    
    public Result<String> getContentType(Map<String,String> map){
        String res = map.get("Content-Type");
        return res != null ? Result.of(res) : Result.of("application/octet-stream");
    }
    
    public String getMetadatasAsString(Map<String,String> map){
        
        return map.entrySet().stream().filter(e -> !e.getKey().equals("X-Parsed-By"))
                .map(e -> e.getKey() + ": " + String.valueOf(e.getValue()) )
                .collect(Collectors.joining("; "));
    } 

}
