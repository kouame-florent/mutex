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
import javax.validation.constraints.NotNull;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Tenant;
import quantum.mutex.dto.FileInfoDTO;


/**
 *
 * @author Florent
 */
@Stateless
public class FileUploadService {

    private static final Logger LOG = Logger.getLogger(FileUploadService.class.getName());
     
    @Inject TikaMetadataService tikaMetadataService;
    @Inject EncryptionService encryptionService;
    @Inject FileService fileService;
    @Inject FileMetadataService fileMetadataService;
//    @Inject FileMetadataDAO fileMetadataDAO;
    @Inject VirtualPageService virtualPageService;
    
    @Asynchronous
    public void handle(@NotNull FileInfoDTO fileInfoDTO){

          tikaMetadataService.handle(fileInfoDTO)
                  .flatMap(fileService::handle)
                  .flatMap(fileMetadataService::handle)
                  .map(fi -> virtualPageService.handle(fi));


        // virtualPageService.handle(dto2); 

        //FileInfoDTO dto2 = fileMetadataService.handle(dto1);
        //Result<FileInfoDTO> dto0 = metadataService.handle(fileInfoDTO);
        //FileInfoDTO dto1 = fileService.handle(dto0);
 
    }
}
