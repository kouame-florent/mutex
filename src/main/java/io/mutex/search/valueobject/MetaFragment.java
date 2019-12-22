/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import java.time.LocalDateTime;


/**
 *
 * @author Florent
 */

public class MetaFragment {
    
    private String inodeUUID;
    private String fileName;
    private String fileOwner;
    private long fileSize;
    private String fileMimeType;
    private String fileGroup;
    private LocalDateTime fileCreated;
    private String content;
    
    
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
	public String getFileOwner() {
		return fileOwner;
	}
	public void setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileMimeType() {
		return fileMimeType;
	}
	public void setFileMimeType(String fileMimeType) {
		this.fileMimeType = fileMimeType;
	}
	public String getFileGroup() {
		return fileGroup;
	}
	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}
	public LocalDateTime getFileCreated() {
		return fileCreated;
	}
	public void setFileCreated(LocalDateTime fileCreated) {
		this.fileCreated = fileCreated;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
    
    
}
