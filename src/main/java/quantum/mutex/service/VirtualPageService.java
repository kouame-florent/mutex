/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import quantum.mutex.domain.Document;
import quantum.mutex.domain.VirtualPage;
import quantum.mutex.domain.dao.VirtualPageDAO;
import quantum.mutex.event.DocumentSavedEvent;
import quantum.mutex.event.qualifier.DocumentSaved;
import quantum.mutex.util.Constants;


/**
 *
 * @author Florent
 */
@Stateless
public class VirtualPageService {

    private static final Logger LOG = Logger.getLogger(VirtualPageService.class.getName());
    
    
    @Inject VirtualPageDAO virtualPageDAO;
    
    public void buildPages(@Observes @DocumentSaved DocumentSavedEvent documentSavedEvent){
        try {
           
            int index = 0;
          
            Document document = documentSavedEvent.getDocument();
            String line;
            TikaInputStream tis = TikaInputStream.get(Files.newInputStream(documentSavedEvent.getFilePath()));
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
                        LOG.log(Level.INFO, "|--| PAGE NUM: {0}", index);
                        savePage(lines, document, index);
                        lines.clear();
                        index++;
                    }
                    
                }
                
                savePage(lines, document, index);
            }
           
            
        } catch (IOException ex) {
            Logger.getLogger(VirtualPageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void savePage(List<String> lines,Document document,int index){
        VirtualPage virtualPage = new VirtualPage();
        virtualPage.setDocument(document);
        String content = lines.stream().collect(Collectors.joining("\n"));
        if(content.length() < Constants.VIRTUAL_PAGE_CHARS_COUNT){
            virtualPage.setContent(content);
            virtualPage.setIndex(index);
            virtualPageDAO.makePersistent(virtualPage);
        }
        
    }
    
   
}
