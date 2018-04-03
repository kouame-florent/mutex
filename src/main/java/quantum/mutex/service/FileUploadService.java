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
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.FileMetadataDAO;

/**
 *
 * @author Florent
 */
@Stateless
public class FileUploadService {

    private static final Logger LOG = Logger.getLogger(FileUploadService.class.getName());
     
    @Inject MetadataService fileMetadataService;
    @Inject EncryptionService encryptionService;
    @Inject FileService documentService;
    @Inject FileMetadataService documentFileMetadataService;
    @Inject FileMetadataDAO documentFileMetadataDAO;
    @Inject VirtualPageService virtualPageService;
    
    @Asynchronous
    public void handle(FileInfoDTO fileInfoDTO){
        FileInfoDTO dto0 = fileMetadataService.handle(fileInfoDTO);
        FileInfoDTO dto1 = documentService.handle(dto0);
        FileInfoDTO dto2 = documentFileMetadataService.handle(dto1);
        virtualPageService.handle(dto2);
 
    }
}
