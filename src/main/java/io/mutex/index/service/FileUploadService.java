/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;



import io.mutex.search.service.DocumentService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.search.valueobject.FileInfo;
import io.mutex.search.valueobject.Metadata;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.index.entity.Inode;
import io.mutex.index.valueobject.Constants;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.index.valueobject.IndexNameSuffix;

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
    @Inject MutexPageService virtualPageService;
    @Inject IndicesService indexService;
    @Inject DocumentService documentService;
    @Inject AnalyzeService analyzeService;
    @Inject TextService textService;
    @Inject EnvironmentUtils envUtils;
    
    @Asynchronous
    public void indexContent(FileInfo fileInfo){

        Map<String,String> tikaMetas = tikaMetadataService.getMetadata(fileInfo.getFilePath());
        Optional<String> oLanguage = tikaMetadataService.getLanguage(tikaMetas);
        
        Optional<String> oRawContent =  tikaContentService.getRawContent(fileInfo,tikaMetas);
        
        oRawContent.ifPresent(c -> LOG.log(Level.INFO, "--> RAW CONTENT LENGHT: {0}", c.length()));
 
        Optional<Inode> oInode = createInode(fileInfo, tikaMetas);
        oRawContent.ifPresent(c -> oInode.ifPresent(i -> indexVirtualPages(c,i,fileInfo)));
        oRawContent.ifPresent(rc -> 
                    oLanguage.ifPresent(lg -> 
                              oInode.ifPresent(in -> 
                                    indexCompletionTerm(rc, lg, in.getUuid(), fileInfo))));
        oInode.ifPresent(i -> indexMetadatas(i, tikaMetas, fileInfo));
        
        oRawContent.ifPresent(rc -> 
                        oInode.ifPresent(in -> 
                                    indexCompletionPhrase(rc,in.getUuid(), fileInfo)));
    }
    
    private Optional<Inode> createInode(FileInfo fileInfo,Map<String,String> tikaMetas){
       Optional<Inode> rInode = inodeService.saveInode(fileInfo,tikaMetas);
       rInode.ifPresent(i -> inodeService.saveInodeGroup(fileInfo.getFileGroup(), i));
       return rInode;
    }
  
    private void indexVirtualPages(String content,Inode inode,FileInfo fileInfo){
        List<VirtualPage> pages = virtualPageService.buildVirtualPages(content,fileInfo.getFileName(), inode);
        virtualPageService.indexVirtualPages(pages, fileInfo.getFileGroup());
    }
    
    private void indexCompletionTerm(String rawContent,String language,String inodeUUID,FileInfo fileInfo){
        List<List<String>> texts =  textService.partition(rawContent, Constants.CONTENT_PARTITION_SIZE);
        LOG.log(Level.INFO, "--> CHILD LISTS SIZE: {0}",texts.size());
                
        List<List<String>> terms = texts.stream()
              .map(txt -> analyzeService.analyzeForTerms(txt,language))
              .collect(toList());
        
        terms.forEach(t -> documentService.indexCompletionTerm(t,fileInfo.getFileGroup(),
                fileInfo.getFileHash(),inodeUUID,IndexNameSuffix.TERM_COMPLETION));
    }
    
    private void indexCompletionPhrase(String rawContent,String inodeUUID,FileInfo fileInfo){
                List<String> phrase = analyzeService
                        .analyzeForPhrase(rawContent,IndexNameSuffix.MUTEX_UTIL);
//                documentService.indexCompletion(phrase, fileInfo.getGroup(),fileInfo.getFileHash(),
//                        IndexNameSuffix.PHRASE_COMPLETION);
              
    }
    
   public void indexMetadatas(Inode inode,Map<String,String> tikaMetas,FileInfo fileInfo){
        Metadata mxMetas = tikaMetadataService.buildMutexMetadata(fileInfo, inode, tikaMetas);
        documentService.indexMetadata(mxMetas, fileInfo.getFileGroup());

   }
    
}
