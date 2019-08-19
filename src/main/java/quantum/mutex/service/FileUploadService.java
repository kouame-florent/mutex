/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;



import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.type.FileInfo;
import quantum.mutex.domain.type.Metadata;
import quantum.mutex.domain.type.VirtualPage;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.service.search.AnalyzeService;
import quantum.mutex.service.search.DocumentService;
import quantum.mutex.service.search.IndexService;
import quantum.mutex.util.Constants;
import quantum.mutex.util.EnvironmentUtils;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.TextService;
import quantum.mutex.util.functional.Result;



/**
 *
 * @author Florent
 */
@Stateless
public class FileUploadService {

    private static final Logger LOG = Logger.getLogger(FileUploadService.class.getName());
     
    @Inject TikaMetadataService tikaMetadataService;
    @Inject TikaContentService tikaContentService;
    @Inject InodeService inodeService;
    @Inject VirtualPageService virtualPageService;
    @Inject IndexService indexService;
    @Inject DocumentService documentService;
    @Inject AnalyzeService analyzeService;
    @Inject TextService textService;
    @Inject EnvironmentUtils envUtils;
    
    @Asynchronous
    public void handle(FileInfo fileInfo){

        Map<String,String> tikaMetas = tikaMetadataService.getMetadata(fileInfo.getFilePath());
        Result<String> rLanguage = tikaMetadataService.getLanguage(tikaMetas);
        Result<String> rContent =  tikaContentService.getRawContent(fileInfo);
        rContent.forEach(c -> LOG.log(Level.INFO, "--> RAW CONTENT LENGHT: {0}", c.length()));
        
        List<List<String>> texts =  rContent.map(c -> textService.partition(c, Constants.CONTENT_PARTITION_SIZE))
               .getOrElse(() -> Collections.EMPTY_LIST);
        LOG.log(Level.INFO, "--> CHILD LISTS SIZE: {0}",texts.size());
        
        List<List<String>> terms = texts.stream()
              .map(txt -> rLanguage
                      .map(l -> analyzeService.analyzeForTerms(txt,l)))
              .filter(r -> r.isSuccess())
              .map(r -> r.successValue())
              .collect(Collectors.toList());
             
        Result<Inode> rInode = inodeService.saveInode(fileInfo,tikaMetas);
        rInode.forEach(i -> inodeService.saveInodeGroup(fileInfo.getFileGroup(), i));
        
        Result<Metadata> rMetadata = 
                rInode.map(i -> tikaMetadataService.buildMutexMetadata(fileInfo, i, tikaMetas));
             
        List<VirtualPage> virtualPages = rContent
                .flatMap(c -> rInode
                    .map(i -> virtualPageService.buildVirtualPages(c, fileInfo.getFileName(), i)))
                .getOrElse(() -> Collections.EMPTY_LIST);
   
        virtualPageService.indexVirtualPages(virtualPages, fileInfo.getFileGroup());
        
        terms.forEach(t -> documentService.indexCompletion(t,fileInfo.getFileGroup(),
                fileInfo.getFileHash(),IndexNameSuffix.TERM_COMPLETION));
              
        rMetadata.forEach(m -> documentService.indexMetadata(m, fileInfo.getFileGroup()));

        
//        List<String> terms = fileInfoWithContent.map(fi -> analyzeService.analyzeForTerms(fi.getRawContent()))
//                .getOrElse(() -> Collections.EMPTY_LIST);
//        
//        List<String> terms = analyzeService.analyzeForTerms(fileInfoWithContent.getRawContent());
        
//        List<String> phrase = analyzeService
//                .analyzeForPhrase(fileInfo.getRawContent(),IndexNameSuffix.MUTEX_UTIL);
//  

//        documentService.indexCompletion(phrase, fileInfo.getGroup(),fileInfo.getFileHash(),
//                IndexNameSuffix.PHRASE_COMPLETION);
//       
  }
}
