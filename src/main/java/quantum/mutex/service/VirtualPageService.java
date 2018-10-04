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
import quantum.mutex.domain.File;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.VirtualPage;
import quantum.mutex.domain.dao.VirtualPageDAO;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.util.Constants;


/**
 *
 * @author Florent
 */
@Stateless
public class VirtualPageService {

    private static final Logger LOG = Logger.getLogger(VirtualPageService.class.getName());
    
    @Inject VirtualPageDAO virtualPageDAO;
    @Inject FileIOService fileIOService;
    
    
    public FileInfoDTO handle(@NotNull FileInfoDTO fileInfoDTO){
//        try {
//            int index = 0;
//            String line;
//            
//            TikaInputStream tis = TikaInputStream.get(Files.newInputStream(fileInfoDTO.getFilePath()));
//            Tika tika = new Tika();
//            
//            try (BufferedReader bufferedReader = new BufferedReader(tika.parse(tis))) {
//                List<String> lines = new ArrayList<>();
//                while( (line = bufferedReader.readLine()) != null){
//                    //LOG.log(Level.INFO, "-->>< CURRENT LINE: {0}", line);
//                   // LOG.log(Level.INFO, "-->>< CURRENT LINE LENGHT: {0}", line.length());
//                    if(!line.isEmpty()){
//                        lines.add(line);
//                    }
//
//                    //LOG.log(Level.INFO, "-->>< LINES SIZE: {0}", lines.size());
//                    if( (lines.size() == Constants.VIRTUAL_PAGE_LINES_COUNT) ){
//                       // LOG.log(Level.INFO, "|--| PAGE NUM: {0}", index);
//                        savePage(lines, fileInfoDTO.getDocument(), index);    
//                        lines.clear();
//                        index++;
//                    }
//                  
//               }
//                if(!lines.isEmpty()){
//                     savePage(lines, fileInfoDTO.getDocument(), index);
//                }
//               
//            }
//            
//        } catch (IOException ex) {
//            Logger.getLogger(VirtualPageService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//       // parseWithOCR(fileInfoDTO);

        
        List<String> documentLines = getTikaInputStream.apply(fileInfoDTO)
                .flatMap(ins -> newTikaObject.apply(Nothing.instance)
                        .flatMap(t -> getReader.apply(ins).apply(t)))
                        .map(b -> getAllLines.apply(b)).getOrElse(ArrayList::new);
        
        List<List<String>> pageLines = createLinesPerPage.apply(documentLines);
        
//        List<Integer> indices = createPageIndices.apply(pageLines);
        
        List<String> contents  = pageLines.stream()
                .map(l -> createVirtualPageContent.apply(l)).collect(Collectors.toList());
        
        List<VirtualPage> pages = IntStream.range(0, contents.size())
                    .mapToObj(i -> new VirtualPage(i, contents.get(i)))
                    .collect(Collectors.toList());
     
        
        List<Result<VirtualPage>> persistedPages = pages.stream().map(p -> provideMutexFile.apply(p).apply(fileInfoDTO.getFile()))
                .map(virtualPageDAO::makePersistent).collect(Collectors.toList());
        
        
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
    
//    private final Function<VirtualPage,Function<String,VirtualPage>> provideContent = vp -> cnt -> {
//        vp.setContent(cnt); return vp;
//    };
//    
//    private final Function<VirtualPage,Function<Integer,VirtualPage>> provideIndex = vp -> idx -> {
//        vp.setIndex(idx); return vp;
//    };
    
    private final Function<VirtualPage,Function<quantum.mutex.domain.File,VirtualPage>> provideMutexFile = vp -> fl -> {
        vp.setFile(fl); return vp;
    };
    
    
     private void savePage(List<String> lines,File file,int index){
        VirtualPage virtualPage = new VirtualPage();
        virtualPage.setFile(file);
        String content = lines.stream()
                .map(line -> line.trim())
                .filter(line -> !line.isEmpty())
                .collect(Collectors.joining("\n"));
        if(content.length() < Constants.VIRTUAL_PAGE_CHARS_COUNT){
            virtualPage.setContent(content);
            virtualPage.setIndex(index);
            virtualPageDAO.makePersistent(virtualPage); 
        }
        
    }
    
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
    
   
    
//    public FileInfoDTO buildPage(FileInfoDTO fileInfoDTO){ //not used
//        try {
//            int index = 0;
//            String line;
//            
//            TikaInputStream tis = TikaInputStream.get(Files.newInputStream(fileInfoDTO.getFilePath()));
//            Tika tika = new Tika();
//            
//            try (BufferedReader bufferedReader = new BufferedReader(tika.parse(tis))) {
//                List<String> lines = new ArrayList<>();
//                while( (line = bufferedReader.readLine()) != null){
//                    //LOG.log(Level.INFO, "-->>< CURRENT LINE: {0}", line);
//                   // LOG.log(Level.INFO, "-->>< CURRENT LINE LENGHT: {0}", line.length());
//                    if(!line.isEmpty()){
//                        lines.add(line);
//                    }
//                    
//                   // LOG.log(Level.INFO, "-->>< LINES SIZE: {0}", lines.size());
//                    if( (lines.size() == Constants.VIRTUAL_PAGE_LINES_COUNT) ){
//                       // LOG.log(Level.INFO, "|--| PAGE NUM: {0}", index);
//                        savePage(lines, fileInfoDTO.getDocument(), index);
//                        lines.clear();
//                        index++;
//                    }
//               }
//                if(!lines.isEmpty()){
//                     savePage(lines, fileInfoDTO.getDocument(), index);
//                }
//               
//            }
//            
//        } catch (IOException ex) {
//            Logger.getLogger(VirtualPageService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return fileInfoDTO;
//    }
    
  
   
    
   
}
