/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template mutexFile, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import io.mutex.shared.service.EncryptionService;

/**
 *
 * @author Florent
 */


public class VirtualPage{
    @Getter @Setter
    private String uuid = UUID.randomUUID().toString();
    
    @Getter @Setter
    private String content;
    
    @Getter @Setter
    private String pageHash;
    
    @Getter @Setter
    private String inodeUUID;
            
    @Getter @Setter
    private String fileName;
    
    @Getter @Setter
    private int totalPageCount;
    
    @Getter @Setter
    private int pageIndex;
    
    @Getter @Setter
    private String permissions;
    
    @Getter @Setter
    private String hash;
 
    public VirtualPage() {
    }
    
    public VirtualPage(String content) {
        this.content = content;
    }

    public VirtualPage(String fileName,int totalPageCount,int pageIndex, String content) {
        this.fileName = fileName;
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
