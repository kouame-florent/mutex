/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author florent
 */
@Stateless
public class EncryptionService {
    
    static final Logger LOG = Logger.getLogger(EncryptionService.class.getName());
	
	
	private final String hashAlgorithm = "SHA-256";
	
	public String hash(String value) {
		String resultHash = null;
		try {
			MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
			byte[] passwordByte = value.getBytes("UTF-8");
			byte[] hash = md.digest(passwordByte);
		        resultHash = Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                
                    LOG.log(Level.SEVERE, e.getMessage());
		}
		
		return resultHash;
	}
        
        public String hash(Path path) {
         
            String resultHash = null;
            try {

                MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
                InputStream inStream = Files.newInputStream(path);

                int byteRead;
                byte[] buffer = new byte[1024];

                while( (byteRead = inStream.read(buffer)) > 0){
                    md.update(buffer, 0, byteRead);
                }

                resultHash = java.util.Base64.getEncoder().encodeToString(md.digest());

            }catch(NoSuchAlgorithmException | IOException ex) {
                Logger.getLogger(EncryptionService.class.getName()).log(Level.SEVERE, null, ex);
            }

        return resultHash;
    }
        
        public String hash(InputStream inputStream) {
         
            String resultHash = null;
            try {

                MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
              
                int byteRead;
                byte[] buffer = new byte[1024];

                while( (byteRead = inputStream.read(buffer)) > 0){
                    md.update(buffer, 0, byteRead);
                }

                resultHash = java.util.Base64.getEncoder().encodeToString(md.digest());

            }catch(NoSuchAlgorithmException | IOException ex) {
                Logger.getLogger(EncryptionService.class.getName()).log(Level.SEVERE, null, ex);
            }

        return resultHash;
    }
      
      
	
	public String hash(byte[] data) {
		String resultHash = null;
		try {
			MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
			byte[] hash = md.digest(data);
			resultHash = Base64.getEncoder().encodeToString(hash);
                } catch (NoSuchAlgorithmException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
		
		return resultHash;
		
	}
	
}
