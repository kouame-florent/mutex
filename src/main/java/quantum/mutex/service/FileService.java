/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;



import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.File;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.FileDAO;


/**
 *
 * @author Florent
 */
@Stateless
public class FileService {

    private static final Logger LOG = Logger.getLogger(FileService.class.getName());
    
    
    @Inject FileDAO documentDAO;
    @Inject File newDocument;
    
    /*
    * Save document and fire DocumentSavedEvent used by VirtualPageService
    */
    public FileInfoDTO handle(FileInfoDTO fileUploadedDTO){
        LOG.log(Level.INFO, "||---|||->>FILE NAME: {0}", fileUploadedDTO.getFileName());
        newDocument.setFileName(fileUploadedDTO.getFileName());
        newDocument.setFileSize(fileUploadedDTO.getFileSize());
        newDocument.setFileContentType(fileUploadedDTO.getFileContentType());
        newDocument.setFileHash(fileUploadedDTO.getFileHash());
        newDocument.setFileLanguage(fileUploadedDTO.getFileLanguage());
        fileUploadedDTO.setDocument(documentDAO.makePersistent(newDocument));
      
       return fileUploadedDTO;
    }
    
}
