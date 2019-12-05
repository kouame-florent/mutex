/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service;



import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import mutex.index.domain.valueobject.FileInfo;
import mutex.search.valueobject.Metadata;
import mutex.search.valueobject.VirtualPage;
import io.mutex.domain.Inode;
import mutex.util.Constants;
import mutex.util.EnvironmentUtils;
import mutex.util.IndexNameSuffix;
import mutex.util.TextService;




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
        Optional<String> rLanguage = tikaMetadataService.getLanguage(tikaMetas);
        Optional<String> rContent =  tikaContentService.getRawContent(fileInfo);
        rContent.ifPresent(c -> LOG.log(Level.INFO, "--> RAW CONTENT LENGHT: {0}", c.length()));
        
        List<List<String>> texts =  rContent.map(c -> textService.partition(c, Constants.CONTENT_PARTITION_SIZE))
               .orElseGet(() -> Collections.EMPTY_LIST);
        LOG.log(Level.INFO, "--> CHILD LISTS SIZE: {0}",texts.size());
        
        List<List<String>> terms = texts.stream()
              .map(txt -> rLanguage.map(l -> analyzeService.analyzeForTerms(txt,l)))
              .flatMap(Optional::stream)
              .collect(toList());
        
        
//        List<List<String>> terms = texts.stream()
//              .map(txt -> rLanguage
//                      .map(l -> analyzeService.analyzeForTerms(txt,l)))
//              .filter(r -> r.isSuccess())
//              .map(r -> r.successValue())
//              .collect(Collectors.toList());
             
        Optional<Inode> rInode = inodeService.saveInode(fileInfo,tikaMetas);
        rInode.ifPresent(i -> inodeService.saveInodeGroup(fileInfo.getFileGroup(), i));
        
        Optional<Metadata> rMetadata = 
                rInode.map(i -> tikaMetadataService.buildMutexMetadata(fileInfo, i, tikaMetas));
             
        List<VirtualPage> virtualPages = rContent
                .flatMap(c -> rInode
                    .map(i -> virtualPageService.buildVirtualPages(c, fileInfo.getFileName(), i)))
                .orElseGet(() -> Collections.EMPTY_LIST);
   
        virtualPageService.indexVirtualPages(virtualPages, fileInfo.getFileGroup());
        
        terms.forEach(t -> documentService.indexCompletion(t,fileInfo.getFileGroup(),
                fileInfo.getFileHash(),IndexNameSuffix.TERM_COMPLETION));
              
        rMetadata.ifPresent(m -> documentService.indexMetadata(m, fileInfo.getFileGroup()));

        
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
