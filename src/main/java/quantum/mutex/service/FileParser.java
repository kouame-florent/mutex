/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.primefaces.model.UploadedFile;
import quantum.mutex.event.FileParsedEvent;
import quantum.mutex.event.SpoolWrittenToEvent;
import quantum.mutex.event.qualifier.FileParsed;
import quantum.mutex.event.qualifier.FileUploaded;
import quantum.mutex.event.qualifier.SpoolWrittenTo;


/**
 *
 * @author Florent
 */
@Stateless
public class FileParser {

    private static final Logger LOG = Logger.getLogger(FileParser.class.getName());
    
    @Inject EncryptionService encryptionService;
    
    @Inject 
    @FileParsed 
    private Event<FileParsedEvent> fileParsedEvent;
   
    /*
    * Parse file in store and fire FileParsedEvent used by FileMetadataService and DocumentService
    */
    public void parse(@Observes @SpoolWrittenTo SpoolWrittenToEvent spoolWrittenToEvent){
           try {
                InputStream inputStream = 
                        Files.newInputStream(spoolWrittenToEvent.getFilePath());
                Tika tika = new Tika();
                Metadata metadata = new Metadata();
                String fileHash = 
                        encryptionService.hash(inputStream);
                Reader reader = tika.parse(inputStream, metadata);
                logMetadatas(metadata);
                fileParsedEvent.fire(new FileParsedEvent(metadata, spoolWrittenToEvent.getFilePath(),fileHash));
            } catch (IOException ex) {
                LOG.log(Level.INFO, "-- PARSING ERROR...");
                LOG.log(Level.SEVERE, null, ex);
                
            }
        
    }
    
     private void logMetadatas(Metadata metadata){
        
        Arrays.stream(metadata.names()).forEach(meta -> { 
            LOG.log(Level.INFO,"-- {0} => {1}", new Object[]{meta,metadata.get(meta)});
        });
        
    }
}
