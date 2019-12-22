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
import io.mutex.shared.service.EncryptionService;

/**
 *
 * @author Florent
 */


public class VirtualPage{
   
    private String uuid = UUID.randomUUID().toString();

    private String content;

    private String pageHash;

    private String inodeUUID;
 
    private String fileName;

    private int totalPageCount;

    private int pageIndex;

    private String permissions;

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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPageHash() {
		return pageHash;
	}

	public void setPageHash(String pageHash) {
		this.pageHash = pageHash;
	}

	public String getInodeUUID() {
		return inodeUUID;
	}

	public void setInodeUUID(String inodeUUID) {
		this.inodeUUID = inodeUUID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
    
    

}
