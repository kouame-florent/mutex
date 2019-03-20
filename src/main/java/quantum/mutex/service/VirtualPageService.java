/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
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
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.service.api.DocumentService;
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
    @Inject DocumentService indexingService;
    @Inject UserGroupService userGroupService;
    
    public Result<FileInfo> index(@NotNull FileInfo fileInfo){
        List<String> documentLines = toList(fileInfo.getRawContent());
        List<List<String>> pageLines = createLinesPerPage.apply(documentLines);
        List<String> contents  = pageLines.stream()
                .map(l -> createVirtualPageContent.apply(l)).collect(Collectors.toList());
        List<VirtualPage> pages = IntStream.range(0, contents.size())
                    .mapToObj(i -> new VirtualPage(pageLines.size(),i, contents.get(i)))
                    .collect(Collectors.toList());
        List<VirtualPage> pagesWithFileRef = pages.stream()
            .map(p -> provideMutexFile.apply(p).apply(fileInfo.getInode()))
            .collect(Collectors.toList());
        
        LOG.log(Level.INFO, "---> PAGES SIZES: {0}", pagesWithFileRef.size());
        
        pagesWithFileRef.stream()
                .forEach(vp -> indexVirtualPages(fileInfo, vp));
        
        return Result.of(fileInfo);
        
    }
    
    private List<String> toList(@NotNull String rawContent){
        return rawContent.lines().collect(Collectors.toList());
    }
     
    private void indexVirtualPages(@NotNull FileInfo fileInfo,@NotNull VirtualPage vp){
            indexingService.indexVirtualPage(fileInfo.getGroup(), vp);
    }
    
    private final Function<List<String>,List<List<String>>> createLinesPerPage =  l ->{
       return ListUtils.partition(l, Constants.VIRTUAL_PAGE_LINES_COUNT);
    };
 
    private final Function<List<String>,String> createVirtualPageContent = ss -> {
        return ss.stream()
                 .map(line -> line.trim())
                 .filter(line -> !line.isEmpty())
                 .collect(Collectors.joining(System.getProperty("line.separator")));
    };
    
    private final Function<VirtualPage,Function<quantum.mutex.domain.entity.Inode,VirtualPage>>
            provideMutexFile = vp -> fl -> {
        vp.setInodeUUID(fl.getUuid().toString()); return vp;
    };
    
}
