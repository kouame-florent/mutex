/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import quantum.functional.api.Result;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.service.FileIOService;
import quantum.mutex.service.FileUploadService;

/**
 *
 * @author Florent
 */
@Named(value = "uploadBacking")
@RequestScoped
public class UploadBacking extends BaseBacking{

    private static final Logger LOG = Logger.getLogger(UploadBacking.class.getName());
    
    @Inject FileUploadService fileUploadService;
    @Inject FileIOService fileIOService;
       
    private UploadedFile file;
    
    /*
    * Fire fileUploadedEvent used by FileIOService writeToSpool method
    */
    public void handleFileUpload(@NotNull FileUploadEvent uploadEvent){
       
            UploadedFile uploadedFile = uploadEvent.getFile();
            LOG.log(Level.INFO, "-->> FILE NAME: {0}", uploadedFile.getFileName());
            LOG.log(Level.INFO, "-->> CONTENT TYPE: {0}", uploadedFile.getContentType());
            LOG.log(Level.INFO, "-->> FILE SIZE: {0}", uploadedFile.getSize());
            
            Result<FileInfo> fileInfoDTO = fileIOService.writeToSpool(uploadedFile);
            fileInfoDTO.forEach(fileUploadService::handle);

    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    
    
}
