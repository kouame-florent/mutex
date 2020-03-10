/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;


import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.collections4.ListUtils;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.user.entity.Group;
import io.mutex.index.entity.Inode;
import io.mutex.index.valueobject.Constants;
import io.mutex.index.valueobject.VirtualPageProperty;
import io.mutex.search.service.LanguageService;
import io.mutex.search.service.SupportedLanguage;

/**
 *
 * @author Florent
 */
@Stateless
public class MutexPageServiceImpl implements MutexPageService {

    private static final Logger LOG = Logger.getLogger(MutexPageServiceImpl.class.getName());
 
    @Inject private DocumentServiceImpl documentService;
    @Inject LanguageService searchLanguageService;
  
    @Override
    public List<VirtualPage> buildVirtualPages( String rawContent,
             String fileName, Inode inode){
        List<String> documentLines = toList(rawContent);
        List<List<String>> pageLines = createLinesPerPage(documentLines);
        
        List<String> contents  = pageLines.stream()
                .map(l -> createVirtualPageContent(l)).collect(Collectors.toList());
        
        List<VirtualPage> pages = IntStream.range(0, contents.size())
            .mapToObj(i -> new VirtualPage(fileName,
                               pageLines.size(),i,contents.get(i)))
            .collect(Collectors.toList());
        
        List<VirtualPage> pagesWithFileRef = pages.stream()
            .map(p -> provideMutexFile(p,inode))
            .collect(Collectors.toList());
        
        return pagesWithFileRef;
     
    }
    
    
    private List<String> toList( String rawContent){
        return rawContent.lines().collect(Collectors.toList());
    }

    @Override
    public void indexVirtualPages(List<VirtualPage> virtualPages,Group group){
        documentService.indexVirtualPage(virtualPages,group);
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
        virtualPage.setInodeUUID(inode.getUuid()); 
        return virtualPage;
    }
    
    @Override
    public String contentMappingProperty(){
        if(searchLanguageService.getCurrentLanguage() == SupportedLanguage.FRENCH){
            return VirtualPageProperty.CONTENT_FR.value();
        }
        return VirtualPageProperty.CONTENT_EN.value();
    }
    
    @Override
     public String trigramMappingProperty(){
       
        return VirtualPageProperty.CONTENT_TRIGRAM.value();
    }
 
}
