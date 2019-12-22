/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;


import java.util.UUID;


/**
 *
 * @author Florent
 */

public class Metadata{
    
    private String uuid = UUID.randomUUID().toString();
    private String inodeUUID;
    private String inodeHash;
    private String fileName;
    private String fileOwner;
    private long fileSize;
    private String fileMimeType;
    private String fileTenant;
    private String fileGroup;
    private long fileCreated;
    private String permissions;
    private String content;

    public Metadata() {
    }
    
    
	@Override
	public String toString() {
		return "Metadata [uuid=" + uuid + "]";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getInodeUUID() {
		return inodeUUID;
	}

	public void setInodeUUID(String inodeUUID) {
		this.inodeUUID = inodeUUID;
	}

	public String getInodeHash() {
		return inodeHash;
	}

	public void setInodeHash(String inodeHash) {
		this.inodeHash = inodeHash;
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

	public String getFileTenant() {
		return fileTenant;
	}

	public void setFileTenant(String fileTenant) {
		this.fileTenant = fileTenant;
	}

	public String getFileGroup() {
		return fileGroup;
	}

	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}

	public long getFileCreated() {
		return fileCreated;
	}

	public void setFileCreated(long fileCreated) {
		this.fileCreated = fileCreated;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
    

}
