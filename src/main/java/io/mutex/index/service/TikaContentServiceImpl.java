/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import io.mutex.search.valueobject.FileInfo;
import java.util.Map;


/**
 *
 * @author Florent
 */
@Stateless
public class TikaContentServiceImpl implements TikaContentService {

    private static final Logger LOG = Logger.getLogger(TikaContentServiceImpl.class.getName());
    
    @Inject TikaServerService tikaServerService;
    
    @Override
    public Optional<String> getRawContent(FileInfo fileInfo,Map<String,String> metas){
        LOG.log(Level.INFO, "--> FILE INFO: {0}", fileInfo);
        Optional<InputStream> ins = openInputStream(fileInfo);
        Optional<String> content = ins.flatMap(in -> tikaServerService.getContent(in,metas))
                .flatMap(res -> toString(res));
        
        content.ifPresent(c -> LOG.log(Level.INFO, "--> CONTENT LENGHT: {0}", c.length())); 
        ins.ifPresent(this::closeInputStream);

        return content;
     }
    
    private Optional<InputStream> openInputStream(FileInfo fileInfo){
        try {
             return Optional.ofNullable(Files.newInputStream(fileInfo.getFilePath()));
        } catch (IOException ex) {
              Logger.getLogger(TikaMetadataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
              return Optional.empty();
        }
    }
    
    private void closeInputStream(InputStream is){
        if(is != null){
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(TikaContentServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Optional<String> toString (Response response){
       return Optional.of(response.readEntity(String.class)) ;
    }
        

}
