/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.shared.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;



/**
 *
 * @author florent
 */
//@Stateless
public class EncryptionService {
    
    static final Logger LOG = Logger.getLogger(EncryptionService.class.getName());
	

    public static String hash( String value) {
        return DigestUtils.sha256Hex(value);

    }

    public static String hash(Path path) throws IOException {
        return DigestUtils.sha256Hex(Files.newInputStream(path));
       
    }
        
    public static String hash( InputStream inputStream) throws IOException {
            
        return DigestUtils.sha256Hex(inputStream);
    }
       
	
    public static String hash(byte[] data) {
        return DigestUtils.sha256Hex(data);

    }
	
}
