/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.search.service;


import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import org.apache.commons.collections4.ListUtils;
import mutex.search.valueobject.VirtualPage;
import mutex.user.domain.entity.Group;
import mutex.index.domain.entity.Inode;
import mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Stateless
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class VirtualPageService {

    private static final Logger LOG = Logger.getLogger(VirtualPageService.class.getName());
 
    @Inject private DocumentService documentService;
  
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
        virtualPage.setInodeUUID(inode.getUuid().toString()); 
        return virtualPage;
    }
 
}
