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
import org.apache.tika.metadata.Metadata;
import org.primefaces.model.UploadedFile;
import quantum.mutex.common.Triplet;
import quantum.mutex.domain.Document;
import quantum.mutex.domain.dao.DocumentDAO;
import quantum.mutex.dto.FileInfoDTO;


/**
 *
 * @author Florent
 */
@Stateless
public class DocumentService {
    
    @Inject DocumentDAO documentDAO;
    @Inject Document newDocument;
    
    
    
    /*
    * Save document and fire DocumentSavedEvent used by VirtualPageService
    */
    public FileInfoDTO handle(FileInfoDTO fileUploadedDTO){
       
       newDocument.setFileName(fileUploadedDTO.getFileName());
       newDocument.setFileSize(fileUploadedDTO.getFileSize());
       newDocument.setFileContentType(fileUploadedDTO.getFileContentType());
       newDocument.setFileHash(fileUploadedDTO.getFileHash());
       newDocument.setFileLanguage(fileUploadedDTO.getFileLanguage());
       fileUploadedDTO.setDocument(documentDAO.makePersistent(newDocument));
       return fileUploadedDTO;
       
    }
    
}
