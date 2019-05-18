/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.service.search.TikaServerService;

/**
 *
 * @author Florent
 */
@Stateless
public class TikaContentService {

    private static final Logger LOG = Logger.getLogger(TikaContentService.class.getName());
    
    @Inject TikaServerService tikaServerService;
    
    public Result<String> getRawContent(@NotNull FileInfo fileInfoDTO){
        LOG.log(Level.INFO, "--> FILE INFO: {0}", fileInfoDTO);
        Result<InputStream> ins = openInputStream.apply(fileInfoDTO);
        Result<String> content = ins.flatMap(in -> tikaServerService.getContent(in))
                .flatMap(res -> toString(res));
        
        content.forEach(c -> LOG.log(Level.INFO, "--> CONTENT LENGHT: {0}", c.length())); 
//        Result<FileInfo> res = content.map(c -> {fileInfoDTO.setRawContent(c);return fileInfoDTO;});
        ins.forEach(closeInputStream);

        return content;
     }
     
    private final Function<FileInfo,Result<InputStream>> openInputStream = fileInfo -> {
         return getInput_(fileInfo.getFilePath());

    };
     
    private Result<InputStream> getInput_(Path path){
        try {
             return Result.success(Files.newInputStream(path));
          } catch (IOException ex) {
              Logger.getLogger(TikaMetadataService.class.getName()).log(Level.SEVERE, null, ex);
              return Result.failure(ex);
          }
    }
    
    private Result<String> toString (Response response){
       return Result.of(response.readEntity(String.class)) ;
    }
        
    private final Effect<InputStream> closeInputStream = in -> {
        try{
            if(in != null) in.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    };

}
