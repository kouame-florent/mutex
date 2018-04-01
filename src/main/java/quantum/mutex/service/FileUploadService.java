/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.dao.DocumentFileMetadataDAO;
import quantum.mutex.dto.FileInfoDTO;

/**
 *
 * @author Florent
 */
@Stateless
public class FileUploadService {

    private static final Logger LOG = Logger.getLogger(FileUploadService.class.getName());
     
    @Inject FileMetadataService fileMetadataService;
    @Inject EncryptionService encryptionService;
    @Inject DocumentService documentService;
    @Inject DocumentFileMetadataService documentFileMetadataService;
    @Inject DocumentFileMetadataDAO documentFileMetadataDAO;
    @Inject VirtualPageService virtualPageService;
    
    @Asynchronous
    public void handle(FileInfoDTO fileInfoDTO){
          
        virtualPageService.handle(documentFileMetadataService
                .handle(documentService.handle(fileMetadataService.handle(fileInfoDTO))));
 
    }
}
