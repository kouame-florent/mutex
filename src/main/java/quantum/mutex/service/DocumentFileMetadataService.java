/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.common.Pair;
import quantum.mutex.domain.Document;
import quantum.mutex.domain.DocumentFileMetadata;
import quantum.mutex.domain.FileMetadata;
import quantum.mutex.domain.dao.DocumentDAO;
import quantum.mutex.domain.dao.DocumentFileMetadataDAO;
import quantum.mutex.domain.dao.FileMetadataDAO;
import quantum.mutex.dto.FileInfoDTO;


/**
 *
 * @author Florent
 */
@Stateless
public class DocumentFileMetadataService {

    private static final Logger LOG = Logger.getLogger(DocumentFileMetadataService.class.getName());
    
        
    @Inject DocumentFileMetadataDAO documentFileMetadataDAO;
    @Inject DocumentDAO documentDAO;
    @Inject FileMetadataDAO fileMetadataDAO;
    
    public FileInfoDTO handle(FileInfoDTO fileInfoDTO){
//        LOG.log(Level.INFO, "-->||>DOCUMENT ID: {0}", docAndMetas._1.getUuid().toString());
        fileInfoDTO.getFileMetadatas().forEach(meta -> {  
            //Document doc = documentDAO.findById(docAndMetas._1.getUuid());
            //FileMetadata met = fileMetadataDAO.findById(meta.getUuid());
            documentFileMetadataDAO.makePersistent(new DocumentFileMetadata(fileInfoDTO.getDocument(), meta));
        });
        return fileInfoDTO;
    }
}
