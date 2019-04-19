/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections4.ListUtils;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.service.search.AnalyzeService;
import quantum.mutex.service.search.DocumentService;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Stateless
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class VirtualPageService {

    private static final Logger LOG = Logger.getLogger(VirtualPageService.class.getName());
 
    @Inject FileIOService fileIOService;
    @Inject DocumentService documentService;
    @Inject UserGroupService userGroupService;
    @Inject AnalyzeService analyzeService;
    
    public Result<FileInfo> index(@NotNull FileInfo fileInfo){
        List<String> documentLines = toList(fileInfo.getRawContent());
        List<List<String>> pageLines = createLinesPerPage(documentLines);
        
        List<String> contents  = pageLines.stream()
                .map(l -> createVirtualPageContent(l)).collect(Collectors.toList());
        
        List<VirtualPage> pages = IntStream.range(0, contents.size())
            .mapToObj(i -> new VirtualPage(fileInfo.getFileName(),
                               pageLines.size(),i,contents.get(i)))
            .collect(Collectors.toList());
        
        List<VirtualPage> pagesWithFileRef = pages.stream()
            .map(p -> provideMutexFile(p,fileInfo.getInode()))
            .collect(Collectors.toList());
        
        List<VirtualPage> pagesWithCompletionTerms = pagesWithFileRef.stream()
                .map(p -> provideAutoCompleteTerms(p,analyzeService
                        .analyzeText(p.getContent(),fileInfo.getFileLanguage())))
                .collect(Collectors.toList());
//        
//        LOG.log(Level.INFO, "---> PAGES SIZES: {0}", pagesWithFileRef.size());
        
        pagesWithCompletionTerms.stream()
                .forEach(vp -> indexVirtualPages(fileInfo, vp));
        
        return Result.of(fileInfo);
    }
    
    private List<String> toList(@NotNull String rawContent){
        return rawContent.lines().collect(Collectors.toList());
    }
     
    private void indexVirtualPages(@NotNull FileInfo fileInfo,@NotNull VirtualPage vp){
        documentService.indexVirtualPage(fileInfo.getGroup(), vp);
    }
    
    private void indexCompletion(@NotNull FileInfo fileInfo,@NotNull VirtualPage vp){
    
    }
    
    private List<List<String>> createLinesPerPage(List<String> lines){
        return ListUtils.partition(lines, Constants.VIRTUAL_PAGE_LINES_COUNT);
    }
 
    
    private String createVirtualPageContent(List<String> lines){
        return lines.stream()
                 .map(line -> line.trim())
                 .filter(line -> !line.isEmpty())
                 .collect(Collectors.joining(System.getProperty("line.separator")));
    }
     
    private VirtualPage provideMutexFile(VirtualPage virtualPage,Inode inode){
        virtualPage.setInodeUUID(inode.getUuid().toString()); 
        return virtualPage;
    }
    
    private VirtualPage provideAutoCompleteTerms(VirtualPage virtualPage,List<String> terms){
        virtualPage.getTermCompletionSuggest().addAll(terms);
        return virtualPage;
    }
 
}
