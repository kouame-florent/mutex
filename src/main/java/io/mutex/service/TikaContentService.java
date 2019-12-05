/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import io.mutex.domain.valueobject.FileInfo;


/**
 *
 * @author Florent
 */
@Stateless
public class TikaContentService {

    private static final Logger LOG = Logger.getLogger(TikaContentService.class.getName());
    
    @Inject TikaServerService tikaServerService;
    
    public Optional<String> getRawContent( FileInfo fileInfoDTO){
        LOG.log(Level.INFO, "--> FILE INFO: {0}", fileInfoDTO);
        Optional<InputStream> ins = openInputStream.apply(fileInfoDTO);
        Optional<String> content = ins.flatMap(in -> tikaServerService.getContent(in))
                .flatMap(res -> toString(res));
        
        content.ifPresent(c -> LOG.log(Level.INFO, "--> CONTENT LENGHT: {0}", c.length())); 
//        Optional<FileInfo> res = content.map(c -> {fileInfoDTO.setRawContent(c);return fileInfoDTO;});
        ins.ifPresent(closeInputStream);

        return content;
     }
     
    private final Function<FileInfo,Optional<InputStream>> openInputStream = fileInfo -> {
         return getInput_(fileInfo.getFilePath());

    };
     
    private Optional<InputStream> getInput_(Path path){
        try {
             return Optional.ofNullable(Files.newInputStream(path));
          } catch (IOException ex) {
              Logger.getLogger(TikaMetadataService.class.getName()).log(Level.SEVERE, null, ex);
              return Optional.empty();
          }
    }
    
    private Optional<String> toString (Response response){
       return Optional.of(response.readEntity(String.class)) ;
    }
        
    private final Consumer<InputStream> closeInputStream = in -> {
        try{
            if(in != null) in.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    };

}
