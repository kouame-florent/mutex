/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;



import java.util.Collections;
import java.util.List;
import quantum.mutex.service.search.CompletionService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.service.search.AnalyzeService;
import quantum.mutex.service.search.DocumentService;
import quantum.mutex.service.search.IndexService;
import quantum.mutex.util.IndexMapping;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.MutexUtilAnalyzer;


/**
 *
 * @author Florent
 */
@Stateless
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class FileUploadService {

    private static final Logger LOG = Logger.getLogger(FileUploadService.class.getName());
     
    @Inject TikaMetadataService tikaMetadataService;
    @Inject TikaContentService tikaContentService;
    @Inject InodeService inodeService;
    @Inject InodeMetadataService inodeMetadataService;
    @Inject VirtualPageService virtualPageService;
//    @Inject CompletionService completionService;
    @Inject IndexService indexService;
    @Inject DocumentService documentService;
    @Inject AnalyzeService analyzeService;
    
    @Asynchronous
    public void handle(FileInfo fileInfo){
        LOG.log(Level.INFO, "--**>> CURRENT GROUP: {0}", fileInfo.getGroup());
        
        List<Metadata> metadatas = tikaMetadataService.handle(fileInfo)
            .flatMap(inodeService::handle)
            .map(inodeMetadataService::buildMetadatas)
            .getOrElse(() -> Collections.EMPTY_LIST);
        
        documentService.indexMetadata(metadatas, fileInfo.getGroup());
        
        tikaContentService.handle(fileInfo)
                .forEach(fi -> virtualPageService.handle(fi));
        
//        List<String> terms = analyzeService.analyzeForTerms(fileInfo.getRawContent());
        
        List<String> phrase = analyzeService
                .analyzeForPhrase(fileInfo.getRawContent(),IndexNameSuffix.MUTEX_UTIL);
        
        
               
//        documentService.indexTermCompletion(terms, fileInfo.getGroup(),fileInfo.getFileHash());
        
//        completionService.index(fileInfo);
//            .forEach(fi -> completionService.index(fi));
  }
}
