/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.Arrays;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import quantum.mutex.domain.FileMetadata;
import quantum.mutex.domain.dao.FileMetadataDAO;
import quantum.mutex.event.FileParsedEvent;
import quantum.mutex.event.qualifier.FileParsed;

/**
 *
 * @author Florent
 */
@Stateless
public class FileMetadataService {
    
     @Inject FileMetadataDAO metadataDAO;
     
     public void save(@Observes @FileParsed FileParsedEvent fileParsedEvent){
        Arrays.stream(fileParsedEvent.getMetadata().names()).forEach(name -> { 
            Optional<FileMetadata> fileMeta
                    = metadataDAO.findByAttributeNameAndAttributeValue(name,fileParsedEvent.getMetadata().get(name));
            if(!fileMeta.isPresent()){
                FileMetadata newFileMeta = new FileMetadata(name, fileParsedEvent.getMetadata().get(name));
                metadataDAO.makePersistent(newFileMeta);
            }
                    
        });
    }
    
}
