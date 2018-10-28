/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import quantum.mutex.service.config.DirectoryVisitor;
import quantum.mutex.service.FileIOService;

/**
 *
 * @author Florent
 */
@Named(value = "parserBacking")
@ViewScoped
public class ParserBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(ParserBacking.class.getName());
    
    @Inject FileIOService fileService;
        
    public void parse(){
   
        FileVisitor<Path> visitor = new DirectoryVisitor<>();
        try {
            Files.walkFileTree(fileService.getSpoolDir(), visitor);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

    }
    
}
