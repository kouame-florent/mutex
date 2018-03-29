/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;
import quantum.mutex.event.SpoolWrittenToEvent;
import quantum.mutex.service.EncryptionService;
import quantum.mutex.event.qualifier.FileUploaded;
import quantum.mutex.event.qualifier.SpoolWrittenTo;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Stateless
public class FileIOService {

    private static final Logger LOG = Logger.getLogger(FileIOService.class.getName());
    
    @Inject EncryptionService encryptionService;
    
    public void createHomeDir(){
       
       if(Files.notExists(getHomeDir())){
           try {
               Files.createDirectories(getHomeDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
      
    }
    
    public void createSpoolDir(){
        
      if(Files.notExists(getSpoolDir())){
           try {
               Files.createDirectories(getSpoolDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
    }
    
    public void createStoreDir(){
     
       if(Files.notExists(getStoreDir())){
           try {
               Files.createDirectories(getStoreDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
    }
  
    public void createIndexDir(){
     
       if(Files.notExists(getIndexDir())){
           try {
               Files.createDirectories(getIndexDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
      
    }
    
    @Inject @SpoolWrittenTo 
    private Event<SpoolWrittenToEvent> spoolWrittenToEvent;
    
    /*
    * Write file to spool and fire SpoolWrittenToEvent used by FileParser
    */
    public void writeToSpool(@Observes @FileUploaded UploadedFile uploadedFile){
        Path filePath = Paths.get(getSpoolDir().toString(),
               Paths.get(UUID.randomUUID().toString()).toString());
        
        if(Files.notExists(filePath)){
          try(OutputStream out = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW);
               InputStream in = uploadedFile.getInputstream();) {
               IOUtils.copy(in, out);
               SpoolWrittenToEvent event  = new SpoolWrittenToEvent();
               event.setFilePath(filePath);
               event.setFileName(uploadedFile.getFileName());
               event.setFileContentType(uploadedFile.getContentType());
               event.setFileSize(uploadedFile.getSize());
               spoolWrittenToEvent.fire(event);
               
           } catch (IOException ex) {
               Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
       
    }
    
    public Optional<Path> writeToStore(UploadedFile uploadedFile){
       String hash = encryptionService.hash(uploadedFile.getContents());
       Path filePath = Paths.get(getCurrentStoreSubDirectory().toString(),
               Paths.get(hash).toString());
       if(Files.notExists(filePath)){
          try(OutputStream out = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW);
               InputStream in = uploadedFile.getInputstream();) {
               IOUtils.copy(in, out);
               return Optional.ofNullable(filePath);
           } catch (IOException ex) {
               Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       return Optional.empty();
    }
    
    private Path getCurrentStoreSubDirectory(){
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = 
                DateTimeFormatter.ofPattern(Constants.STORE_SUB_DIR_NAME_DATE_FORMAT);
        Path todayPath = Paths.get(getStoreDir().toString(), 
                Paths.get(today.format(formatter)).toString());
        if(Files.notExists(todayPath)){
            try {
               return Files.createDirectories(todayPath);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return todayPath;        
    }
    
    public Path getHomeDir(){
        return Paths.get(Constants.APPLICATION_HOME_DIR);
    }
    
    public Path getSpoolDir(){
        return Paths.get(Constants.APPLICATION_SPOOL_DIR);
    }
    
    public Path getStoreDir(){
        return Paths.get(Constants.APPLICATION_STORE_DIR);
    }
    
    public Path getIndexDir(){
        return Paths.get(Constants.APPLICATION_INDEXES_DIR);
    }
    
}
