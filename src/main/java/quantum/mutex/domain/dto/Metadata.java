/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;



import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import quantum.mutex.service.EncryptionService;

/**
 *
 * @author Florent
 */

@Getter @Setter  @ToString
public class Metadata{
    
    private String uuid = UUID.randomUUID().toString();
    private String attributeName;
    private String attributeValue;
    private String inodeUUID;
    private String inodeHash;
    private String fileName;
    private String fileOwner;
    private long fileSize;
    private String fileTenant;
    private String fileGroup;
    private LocalDateTime fileCreated;
    private String permissions;
    private String hash;

    public Metadata() {
    }

    public Metadata(String name, Object value) {
        this.attributeName = name;
        if(value instanceof String){
            this.attributeValue = (String)value;
        }
        if(value instanceof List){
            this.attributeValue = String.join(";",((List<String>)value));
        }
        
        this.hash = buildHash();
    }
    
    private String buildHash(){
        try {
            String tmpHash = EncryptionService.hash(attributeValue + attributeName
                    + inodeHash);
            return Base64.getEncoder().encodeToString(tmpHash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Metadata.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
   
}
