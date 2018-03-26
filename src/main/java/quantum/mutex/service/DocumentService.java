/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import quantum.mutex.domain.Document;
import quantum.mutex.domain.dao.DocumentDAO;
import quantum.mutex.event.DocumentSavedEvent;
import quantum.mutex.event.FileParsedEvent;
import quantum.mutex.event.qualifier.DocumentSaved;
import quantum.mutex.event.qualifier.FileParsed;

/**
 *
 * @author Florent
 */
@Stateless
public class DocumentService {
    
    @Inject DocumentDAO documentDAO;
    @Inject EncryptionService encryptionService;
    
    @Inject Document newDocument;
    
    @Inject
    @DocumentSaved
    private Event<DocumentSavedEvent> documentSavedEvent;
    
    /*
    * Save document and fire DocumentSavedEvent used by VirtualPageService
    */
    public void save(@Observes @FileParsed FileParsedEvent fileParsedEvent){
        
       newDocument.setFileHash(fileParsedEvent.getFileHash());
       Document savedDocument = documentDAO.makePersistent(newDocument);
       documentSavedEvent.fire(new DocumentSavedEvent(savedDocument, 
               fileParsedEvent.getFilePath()));
    }
    
}
