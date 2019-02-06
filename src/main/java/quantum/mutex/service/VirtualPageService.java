/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import quantum.functional.api.Nothing;
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
    
//    @Resource(name = "DefaultManagedExecutorService")
//    private ManagedExecutorService executorService;
//  
    @Inject FileIOService fileIOService;
    @Inject DocumentService indexingService;
    @Inject UserGroupService userGroupService;
    
    public Result<FileInfo> index(@NotNull FileInfo fileInfo){
      
        List<String> documentLines = getTikaInputStream(fileInfo)
                .flatMap(ins -> newTikaObject.apply(Nothing.instance)
                        .flatMap(t -> getReader.apply(ins).apply(t)))
                        .map(b -> getAllLines.apply(b)).getOrElse(ArrayList::new);
        
        List<List<String>> pageLines = createLinesPerPage.apply(documentLines);
        
        List<String> contents  = pageLines.stream()
                .map(l -> createVirtualPageContent.apply(l)).collect(Collectors.toList());
        
        List<VirtualPage> pages = IntStream.range(0, contents.size())
                    .mapToObj(i -> new VirtualPage(i, contents.get(i)))
                    .collect(Collectors.toList());
       
        List<VirtualPage> pagesWithFileRef = pages.stream()
            .map(p -> provideMutexFile.apply(p).apply(fileInfo.getInode()))
            .collect(Collectors.toList());
        
        LOG.log(Level.INFO, "---> PAGES SIZES: {0}", pagesWithFileRef.size());
        
        pagesWithFileRef.stream()
                .forEach(vp -> indexVirtualPages(fileInfo, vp));
        
        return Result.of(fileInfo);
        
    }
    

     
    private void indexVirtualPages(FileInfo fileInfo,VirtualPage vp){
            indexingService.indexVirtualPage(fileInfo.getGroup(), vp);
    }
    
    private Result<TikaInputStream> getTikaInputStream(FileInfo fileInfo){
        try{
                TikaInputStream tis = TikaInputStream.get(fileInfo.getFilePath());
                return Result.success(tis);
            }catch(IOException ex){
                LOG.log(Level.SEVERE, ex.getMessage());
                return Result.failure(ex);
            }
    }
    
//    private final Function<FileInfo,Result<TikaInputStream>> getTikaInputStream = fi -> {
//        return fi.getFilePath().flatMap((Path p) -> {
//            try{
//                TikaInputStream tis = TikaInputStream.get(p);
//                return Result.success(tis);
//            }catch(IOException ex){
//                LOG.log(Level.SEVERE, ex.getMessage());
//                return Result.failure(ex);
//            }
//          }
//        );
//         
//    };
    
    private final Function<Nothing,Result<Tika>> newTikaObject = n -> {
        return Result.success(new Tika());
    };
    
    private final Function<TikaInputStream,Function<Tika,Result<BufferedReader>>> getReader = tis -> tika ->{
        try{
            return Result.success(new BufferedReader(tika.parse(tis)));
        }catch(IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage());
            return Result.failure(ex);
        }
        
    };
    
    private final Function<BufferedReader,List<String>> getAllLines = buf ->{
        
       List<String> ss = buf.lines().filter(l -> !l.isEmpty())
               .collect(Collectors.toList());
       try{
           buf.close();
       }catch(IOException ex){
           LOG.log(Level.SEVERE, ex.getMessage());
       }
       return ss;
    };
    
    private final Function<List<String>,List<List<String>>> createLinesPerPage =  l ->{
       return ListUtils.partition(l, Constants.VIRTUAL_PAGE_LINES_COUNT);
    };
    
//    private final Function<List<?>, List<Integer>> createPageIndices = l ->{
//       return  IntStream.range(0,l.size()).boxed().collect(Collectors.toList());
//    };
//    
    
//    private final Function<Nothing,Result<VirtualPage>> newVirtualPage = n -> {
//        return Result.success(new VirtualPage());
//    };
//    
    private final Function<List<String>,String> createVirtualPageContent = ss -> {
        return ss.stream()
                 .map(line -> line.trim())
                 .filter(line -> !line.isEmpty())
                 .collect(Collectors.joining(System.getProperty("line.separator")));
    };
    
    
    private final Function<VirtualPage,Function<quantum.mutex.domain.entity.Inode,VirtualPage>>
            provideMutexFile = vp -> fl -> {
        vp.setMutexFileUUID(fl.getUuid().toString()); return vp;
    };
    
  
    
    private void parseWithOCR(FileInfo fileInfoDTO){ //not used
//        try(InputStream inputStream = Files.newInputStream(fileInfoDTO.getFilePath());
//                OutputStream outputStream = Files.newOutputStream(fileIOService.getRandomPath());) {
//            
//            TikaConfig tikaConfig = new TikaConfig();
//            BodyContentHandler handler = new BodyContentHandler(outputStream);
//            Parser parser = new AutoDetectParser(tikaConfig);
//            Metadata meta = new Metadata();
//            ParseContext parsecontext = new ParseContext();
//            
//            PDFParserConfig pdfConfig = new PDFParserConfig();
//            pdfConfig.setExtractInlineImages(true);
//
//            TesseractOCRConfig tesserConfig = new TesseractOCRConfig();
//            tesserConfig.setLanguage("eng+fra");
//            tesserConfig.setTesseractPath("C:/Program Files (x86)/Tesseract-OCR");
//
//            parsecontext.set(Parser.class, parser);
//            parsecontext.set(PDFParserConfig.class, pdfConfig);
//            parsecontext.set(TesseractOCRConfig.class, tesserConfig);
//            
//            parser.parse(inputStream, handler, meta, parsecontext);
//            
//
//        } catch (TikaException | IOException | SAXException ex) {
//            Logger.getLogger(VirtualPageService.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    

   
}
