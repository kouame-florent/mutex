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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
import quantum.mutex.domain.DocumentFile;
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
    
    public FileInfoDTO handle(FileInfoDTO fileInfoDTO){
        try {
            int index = 0;
            String line;
            
            TikaInputStream tis = TikaInputStream.get(Files.newInputStream(fileInfoDTO.getFilePath()));
            Tika tika = new Tika();
            
            try (BufferedReader bufferedReader = new BufferedReader(tika.parse(tis))) {
                List<String> lines = new ArrayList<>();
                while( (line = bufferedReader.readLine()) != null){
                    //LOG.log(Level.INFO, "-->>< CURRENT LINE: {0}", line);
                   // LOG.log(Level.INFO, "-->>< CURRENT LINE LENGHT: {0}", line.length());
                    if(!line.isEmpty()){
                        lines.add(line);
                    }
                    
                    LOG.log(Level.INFO, "-->>< LINES SIZE: {0}", lines.size());
                    if( (lines.size() == Constants.VIRTUAL_PAGE_LINES_COUNT) ){
                       // LOG.log(Level.INFO, "|--| PAGE NUM: {0}", index);
                        savePage(lines, fileInfoDTO.getDocument(), index);
                        lines.clear();
                        index++;
                    }
               }
                if(!lines.isEmpty()){
                     savePage(lines, fileInfoDTO.getDocument(), index);
                }
               
            }
            
        } catch (IOException ex) {
            Logger.getLogger(VirtualPageService.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return fileInfoDTO;
    }
    
    private void parseWithOCR(FileInfoDTO fileInfoDTO){ //not used
        try(InputStream inputStream = Files.newInputStream(fileInfoDTO.getFilePath())) {
            
            OutputStream outputStream = Files.newOutputStream(fileIOService.getRandomPath());
            
            TikaConfig tikaConfig = new TikaConfig();
            BodyContentHandler handler = new BodyContentHandler(outputStream);
            Parser parser = new AutoDetectParser(tikaConfig);
            Metadata meta = new Metadata();
            ParseContext parsecontext = new ParseContext();
            
            PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setExtractInlineImages(true);

            TesseractOCRConfig tesserConfig = new TesseractOCRConfig();
            tesserConfig.setLanguage("eng+fra");
            tesserConfig.setTesseractPath("C:/Program Files (x86)/Tesseract-OCR");

            parsecontext.set(Parser.class, parser);
            parsecontext.set(PDFParserConfig.class, pdfConfig);
            parsecontext.set(TesseractOCRConfig.class, tesserConfig);
            
            parser.parse(inputStream, handler, meta, parsecontext);
            

        } catch (TikaException | IOException | SAXException ex) {
            Logger.getLogger(VirtualPageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public FileInfoDTO buildPage(FileInfoDTO fileInfoDTO){ //not used
        try {
            int index = 0;
            String line;
            
            TikaInputStream tis = TikaInputStream.get(Files.newInputStream(fileInfoDTO.getFilePath()));
            Tika tika = new Tika();
            
            try (BufferedReader bufferedReader = new BufferedReader(tika.parse(tis))) {
                List<String> lines = new ArrayList<>();
                while( (line = bufferedReader.readLine()) != null){
                    //LOG.log(Level.INFO, "-->>< CURRENT LINE: {0}", line);
                   // LOG.log(Level.INFO, "-->>< CURRENT LINE LENGHT: {0}", line.length());
                    if(!line.isEmpty()){
                        lines.add(line);
                    }
                    
                    LOG.log(Level.INFO, "-->>< LINES SIZE: {0}", lines.size());
                    if( (lines.size() == Constants.VIRTUAL_PAGE_LINES_COUNT) ){
                       // LOG.log(Level.INFO, "|--| PAGE NUM: {0}", index);
                        savePage(lines, fileInfoDTO.getDocument(), index);
                        lines.clear();
                        index++;
                    }
               }
                if(!lines.isEmpty()){
                     savePage(lines, fileInfoDTO.getDocument(), index);
                }
               
            }
            
        } catch (IOException ex) {
            Logger.getLogger(VirtualPageService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileInfoDTO;
    }
    
  
    private void savePage(List<String> lines,DocumentFile document,int index){
        VirtualPage virtualPage = new VirtualPage();
        virtualPage.setDocument(document);
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
    
   
}
