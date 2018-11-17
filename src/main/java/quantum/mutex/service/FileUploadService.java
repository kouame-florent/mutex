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
    @Inject VirtualPageService virtualPageService;
    
    @Asynchronous
    public void handle(@NotNull FileInfoDTO fileInfoDTO){

        tikaMetadataService
            .handle(fileInfoDTO)
            .flatMap(fileService::handle)
            .flatMap(fileMetadataService::index)
            .map(fi -> virtualPageService.index(fi));
  }
}
