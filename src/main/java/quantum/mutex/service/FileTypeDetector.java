/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.File;
import java.io.IOException;
import javax.ejb.Stateless;
import org.apache.tika.Tika;

/**
 *
 * @author Florent
 */
@Stateless
public class FileTypeDetector {
    
    private final Tika tika = new Tika();
    
    public String detectType(File file) throws IOException{
        return tika.detect(file);
    }
}
