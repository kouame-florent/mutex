/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import quantum.mutex.event.qualifier.FileUploaded;
import quantum.mutex.event.qualifier.SpoolWrittenTo;
import quantum.mutex.service.file.DirectoryVisitor;
import quantum.mutex.service.FileIOService;

/**
 *
 * @author Florent
 */
@Named(value = "uploadBacking")
@RequestScoped
public class UploadBacking extends BaseBacking{

    private static final Logger LOG = Logger.getLogger(UploadBacking.class.getName());
    
   @Inject @FileUploaded private Event<UploadedFile> fileUploadedEvent;
   
    private UploadedFile file;
    
    /*
    * Fire fileUploadedEvent used by FileIOService writeToSpool method
    */
    public void handleFileUpload(FileUploadEvent uploadEvent){
        UploadedFile uploadedFile = uploadEvent.getFile();
        LOG.log(Level.INFO, "-->> FILE NAME: {0}", uploadedFile.getFileName());
        LOG.log(Level.INFO, "-->> CONTENT TYPE: {0}", uploadedFile.getContentType());
        LOG.log(Level.INFO, "-->> FILE SIZE: {0}", uploadedFile.getSize());
        
        fileUploadedEvent.fire(uploadedFile);
       
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    
    
}
