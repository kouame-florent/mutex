/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;



import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.service.search.AnalyzeService;
import quantum.mutex.service.search.DocumentService;
import quantum.mutex.service.search.IndexService;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.TextService;



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
    @Inject IndexService indexService;
    @Inject DocumentService documentService;
    @Inject AnalyzeService analyzeService;
    @Inject TextService textService;
    
    @Asynchronous
    public void handle(FileInfo fileInfo){
//        LOG.log(Level.INFO, "--**>> RAW TEXT : {0}", fileInfo.getRawContent());
            
        Result<FileInfo> fileInfoWithMetas = tikaMetadataService.handle(fileInfo);
        Result<FileInfo> persistentFileInfo = fileInfoWithMetas.flatMap(inodeService::handle);
       
//        List<Metadata> metadatas = persistentFileInfo.map(inodeMetadataService::handle)
//                .getOrElse(() -> Collections.EMPTY_LIST);
//   
//        documentService.indexMetadata(metadatas, fileInfo.getGroup());
        
        Result<FileInfo> fileInfoWithContent = persistentFileInfo.flatMap(tikaContentService::handle);
//        fileInfoWithContent.forEach(fi -> virtualPageService.handle(fi));
        
        fileInfoWithContent.map(fi -> textService.partition(fi.getRawContent(), 5000))
                .forEach(l -> LOG.log(Level.INFO, "LIST OF LIST SIZE: {0}",l.size()));
 
        List<String> terms = fileInfoWithContent.map(fi -> analyzeService.analyzeForTerms(fi.getRawContent()))
                .getOrElse(() -> Collections.EMPTY_LIST);
//        
//        List<String> terms = analyzeService.analyzeForTerms(fileInfoWithContent.getRawContent());
        
//        List<String> phrase = analyzeService
//                .analyzeForPhrase(fileInfo.getRawContent(),IndexNameSuffix.MUTEX_UTIL);
//                     
//        documentService.indexCompletion(terms, fileInfo.getGroup(),fileInfo.getFileHash(),
//                IndexNameSuffix.TERM_COMPLETION);
//        
//        documentService.indexCompletion(phrase, fileInfo.getGroup(),fileInfo.getFileHash(),
//                IndexNameSuffix.PHRASE_COMPLETION);
//       
  }
}
