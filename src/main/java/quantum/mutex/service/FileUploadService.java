/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;



import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import quantum.mutex.domain.dto.FileInfo;


/**
 *
 * @author Florent
 */
@Stateless
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class FileUploadService {

    private static final Logger LOG = Logger.getLogger(FileUploadService.class.getName());
     
    @Inject TikaMetadataService tikaMetadataService;
    @Inject FileService fileService;
    @Inject FileMetadataService fileMetadataService;
    @Inject VirtualPageService virtualPageService;
    
    @Asynchronous
    public void handle(FileInfo fileInfo){
        tikaMetadataService
            .handle(fileInfo)
            .flatMap(fileService::handle)
            .flatMap(fileMetadataService::index)
            .map(fi -> virtualPageService.index(fi))
            .forEach(m -> LOG.log(Level.INFO,"-- {0}...", m));
  }
}
