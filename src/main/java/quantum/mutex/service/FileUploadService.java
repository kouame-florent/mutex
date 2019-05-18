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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.entity.InodeGroup;
import quantum.mutex.service.search.AnalyzeService;
import quantum.mutex.service.search.DocumentService;
import quantum.mutex.service.search.IndexService;
import quantum.mutex.util.Constants;
import quantum.mutex.util.EnvironmentUtils;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.TextService;



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
    @Inject FileInfoService fileInfoService;
    @Inject InodeMetadataService inodeMetadataService;
    @Inject VirtualPageService virtualPageService;
    @Inject IndexService indexService;
    @Inject DocumentService documentService;
    @Inject AnalyzeService analyzeService;
    @Inject TextService textService;
    @Inject EnvironmentUtils envUtils;
    
    @Asynchronous
    public void handle(FileInfo fileInfo){
//        LOG.log(Level.INFO, "--**>> RAW TEXT : {0}", fileInfo.getRawContent());
            
        Map<String,String> tikaMetas = tikaMetadataService.getMetadata(fileInfo.getFilePath());
        Result<String> rLanguage = tikaMetadataService.getLanguage(tikaMetas);
        
        Result<Inode> rInode = inodeService.saveInode(fileInfo,tikaMetas);
        rInode.forEach(i -> inodeService.saveInodeGroup(fileInfo.getFileGroup(), i));

        Result<String> rContent =  tikaContentService.getRawContent(fileInfo);
        rContent.forEach(c -> LOG.log(Level.INFO, "--> RAW CONTENT LENGHT: {0}", c.length()));
     
        List<List<String>> texts =  rContent.map(c -> textService.partition(c, Constants.CONTENT_PARTITION_SIZE))
               .getOrElse(() -> Collections.EMPTY_LIST);
        LOG.log(Level.INFO, "--> CHILD LISTS SIZE: {0}",texts.size());

        List<List<String>> terms = texts.stream()
                .map(txt -> rLanguage.map(l -> analyzeService.analyzeForTerms(txt,l)))
                .filter(r -> r.isSuccess())
                .map(r -> r.successValue())
                .collect(Collectors.toList());
        
        terms.forEach(t -> documentService.indexCompletion(t, 
                  fileInfo.getFileGroup(),fileInfo.getFileHash(), 
                  IndexNameSuffix.TERM_COMPLETION));
        
//        Result<FileInfo> fileInfoWithContent = 
//                rContent.flatMap(c -> fileInfoWithInode.flatMap(fi -> fileInfoService.addRawContent(fi, c) ));
//        
//        List<VirtualPage> virtualPages = fileInfoWithContent
//                .map(fi -> virtualPageService.buildVirtualPages(fi))
//                .getOrElse(Collections::emptyList);
        
        List<VirtualPage> virtualPages = rContent.flatMap(c -> rInode
                .map(i -> virtualPageService.buildVirtualPages(c, fileInfo.getFileName(), i)))
                .getOrElse(() -> Collections.EMPTY_LIST);
//        virtualPageService.buildVirtualPages(rawContent, fileName, inode);
       
        virtualPageService.indexVirtualPages(virtualPages, fileInfo.getFileGroup());
        
//        rMetadata.forEachOrException(m -> documentService.indexMetadata(m,fileInfo.getGroup()));
        
        Result<Metadata> rMetadata = 
                rInode.map(i -> tikaMetadataService.buildMutexMetadata(fileInfo, i, tikaMetas));
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
