/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.entity;


import io.mutex.shared.entity.BaseEntity;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.User;
import java.util.BitSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;




/**
 *
 * @author Florent
 */

@NamedQueries({
    @NamedQuery(
        name = "Inode.findByHash",
        query = "SELECT i FROM Inode i WHERE i.fileHash = :fileHash"
    ),
    @NamedQuery(
        name = "Inode.findByOwner",
        query = "SELECT i FROM Inode i WHERE i.ownerUser = :ownerUser"
    ),
    
})
@Table(name = "mx_inode")
@Entity
public class Inode extends BaseEntity{
  
    /**
    * 
    */
   private static final long serialVersionUID = 1L;

   @Column(length = 1000,nullable = false)
   private String fileHash;
   
   
   private String fileContentType;
 
   @Column(length = 500)
   private String fileName;
   
   
   private long fileSize;
   
   @Column(length = 1000)
   private String filePath;
      
   private String fileLanguage;
     
   @ManyToOne
   private User ownerUser;
  
   private BitSet permissions = new BitSet(9);
   
   public Inode(String filHash){
        this();
        this.fileHash = filHash;
    }

    public Inode(String fileHash, String fileContentType, String fileName, 
            long fileSize, String filePath, String fileLanguage, User ownerUser) {
        this.fileHash = fileHash;
        this.fileContentType = fileContentType;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileLanguage = fileLanguage;
        this.ownerUser = ownerUser;
        
    }
   
    
    
    public Inode(Inode other,Group group){
        this.fileHash = other.fileHash;
        this.edited = other.edited;
        this.fileContentType = other.fileContentType;
        this.fileLanguage = other.fileLanguage;
        this.fileSize = other.fileSize;
        this.ownerUser = other.ownerUser;
        this.permissions = other.permissions;
//        this.ownerGroup = group;
    }
      
    public Inode() {

    }

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileLanguage() {
		return fileLanguage;
	}

	public void setFileLanguage(String fileLanguage) {
		this.fileLanguage = fileLanguage;
	}

	public User getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(User ownerUser) {
		this.ownerUser = ownerUser;
	}

	public BitSet getPermissions() {
		return permissions;
	}

	public void setPermissions(BitSet permissions) {
		this.permissions = permissions;
	}
   
    
}
