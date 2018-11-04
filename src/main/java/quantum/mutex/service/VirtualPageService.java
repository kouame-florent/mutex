/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections4.ListUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import quantum.mutex.common.Nothing;
import quantum.mutex.common.Result;
import quantum.mutex.common.Stream;
import quantum.mutex.common.Tuple;
import quantum.mutex.domain.MutexFile;
import quantum.mutex.domain.Tenant;
import quantum.mutex.dto.VirtualPageDTO;
import quantum.mutex.domain.dao.VirtualPageDAO;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.service.elastic.IndexingService;
import quantum.mutex.util.Constants;


/**
 *
 * @author Florent
 */
@Stateless
public class VirtualPageService {

    private static final Logger LOG = Logger.getLogger(VirtualPageService.class.getName());
    
//    @Inject VirtualPageDAO virtualPageDAO;
    @Inject FileIOService fileIOService;
    @Inject IndexingService indexingService;
    
    
    public FileInfoDTO index(@NotNull FileInfoDTO fileInfoDTO){
      
        List<String> documentLines = getTikaInputStream.apply(fileInfoDTO)
                .flatMap(ins -> newTikaObject.apply(Nothing.instance)
                        .flatMap(t -> getReader.apply(ins).apply(t)))
                        .map(b -> getAllLines.apply(b)).getOrElse(ArrayList::new);
        
        List<List<String>> pageLines = createLinesPerPage.apply(documentLines);
        
        List<String> contents  = pageLines.stream()
                .map(l -> createVirtualPageContent.apply(l)).collect(Collectors.toList());
        
        List<VirtualPageDTO> pages = IntStream.range(0, contents.size())
                    .mapToObj(i -> new VirtualPageDTO(i, contents.get(i)))
                    .collect(Collectors.toList());
        
        pages.stream()
            .map(p -> provideMutexFile.apply(p).apply(fileInfoDTO.getFile()))
            .forEach(vp -> indexingService.indexingVirtualPage(fileInfoDTO.getFile().getOwnerGroup(), 
                    vp));
        
//                .map(vp -> virtualPageDAO.makePersistent(vp)).collect(Collectors.toList());
        
        
        return fileInfoDTO;
    }
    
    
    private final Function<FileInfoDTO,Result<TikaInputStream>> getTikaInputStream = fi -> {
        return fi.getFilePath().flatMap((Path p) -> {
            try{
                TikaInputStream tis = TikaInputStream.get(p);
                return Result.success(tis);
            }catch(IOException ex){
                LOG.log(Level.SEVERE, ex.getMessage());
                return Result.failure(ex);
            }
          }
        );
         
    };
    
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
    
    
    private final Function<VirtualPageDTO,Function<quantum.mutex.domain.MutexFile,VirtualPageDTO>>
            provideMutexFile = vp -> fl -> {
        vp.setMutexFileUUID(fl.getUuid().toString()); return vp;
    };
    
    
//     private void savePage(List<String> lines,MutexFile file,int index){
//        VirtualPageDTO virtualPage = new VirtualPageDTO();
//        virtualPage.setMutexFileUUID(file.getUuid().toString());
//        String content = lines.stream()
//                .map(line -> line.trim())
//                .filter(line -> !line.isEmpty())
//                .collect(Collectors.joining("\n"));
//        if(content.length() < Constants.VIRTUAL_PAGE_CHARS_COUNT){
//            virtualPage.setContent(content);
//            virtualPage.setPageIndex(index);
//            virtualPageDAO.makePersistent(virtualPage); 
//        }
//        
//    }
    
    private void parseWithOCR(FileInfoDTO fileInfoDTO){ //not used
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
