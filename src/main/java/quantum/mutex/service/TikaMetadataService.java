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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.service.search.TikaServerService;


/**
 *
 * @author Florent
 */
@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
public class TikaMetadataService {
   
    private static final Logger LOG = Logger.getLogger(TikaMetadataService.class.getName());
    
    @Inject TikaServerService tss;
   
    public Result<FileInfo> handle(@NotNull FileInfo fileInfoDTO){
        
        Result<InputStream> ins = openInputStream.apply(fileInfoDTO);
        Map<String,String> metas = ins.flatMap(in -> tss.getMetas(in))
                .flatMap(res -> toJson(res))
                .map(json -> unmarshallToMap(json))
                .getOrElse(() -> Collections.EMPTY_MAP);
        
        Metadata dto = toMetasDTO(metas);
        fileInfoDTO.setFileMetadata(dto);
        getContentType(metas).forEach(c -> fileInfoDTO.setFileContentType(c));
        getLanguage(metas).forEach(l -> fileInfoDTO.setFileLanguage(l));
        
        ins.forEach(closeInputStream);

        return Result.of(fileInfoDTO);
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
    
    private Metadata toMetasDTO(Map<String,String> map){
       Metadata meta = new Metadata();
       meta.setContent(metadataContent(map));
       return meta;
    }
    
    private String metadataContent(Map<String,String> map){
        return map.entrySet().stream().filter(e -> !e.getKey().equals("X-Parsed-By"))
                .map(e -> e.getKey() + ": " + e.getValue() )
                .collect(Collectors.joining(";"));
    }
    
    private String toSingleValue(List<String> values){
        return String.join(";", values);
    }
    
    private Result<String> getContentType(Map<String,String> map){
        String res = map.get("Content-Type");
        return res != null ? Result.of(res) : Result.of("application/octet-stream");
    }
    
    private Result<String> getLanguage(Map<String,String> map){
        String res = map.get("language");
        return res != null ? Result.of(res) : Result.of("fr");
    }
         
    private final Function<FileInfo,Result<InputStream>> openInputStream = fileInfo -> {
         return getInput_(fileInfo.getFilePath());
//       return fileInfoDTO.getFilePath().flatMap(this::getInput_);
    };
    
    private Result<InputStream> getInput_(Path path){
        try {
             return Result.success(Files.newInputStream(path));
          } catch (IOException ex) {
              Logger.getLogger(TikaMetadataService.class.getName()).log(Level.SEVERE, null, ex);
              return Result.failure(ex);
          }
    }
    
    private final Effect<InputStream> closeInputStream = in -> {
        try{
            if(in != null) in.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    };

   
}
