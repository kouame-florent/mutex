/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template mutexFile, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import quantum.mutex.service.EncryptionService;




/**
 *
 * @author Florent
 */

@Getter @Setter
public class VirtualPage{
    
    private String uuid = UUID.randomUUID().toString();
    private String content;
    private String pageHash;
    private String inodeUUID;
    private int totalPageCount;
    private int pageIndex;
    private String permissions;
    private String hash;
    
    public VirtualPage() {
    }
    
    public VirtualPage(String content) {
        this.content = content;
    }

    public VirtualPage(int totalPageCount,int pageIndex, String content) {
        this.totalPageCount = totalPageCount;
        this.pageIndex = pageIndex;
        this.content = content;
        this.hash = buildHash();
    }
    
    private String buildHash(){
        try {
            String tmpHash = EncryptionService.hash(content);
            return Base64.getEncoder().encodeToString(tmpHash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(VirtualPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

}
