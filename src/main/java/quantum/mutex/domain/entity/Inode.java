/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.entity;


import java.util.BitSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 *
 * @author Florent
 */

@NamedQueries({
    @NamedQuery(
        name = "Inode.findByHash",
        query = "SELECT i FROM Inode i WHERE i.fileHash = :fileHash"
    ),
    
})
@Table(name = "mx_inode")
@Entity
public class Inode extends BaseEntity{
  
   @NotNull
   @Column(length = 1000)
   private String fileHash;
   
   @NotNull
   private String fileContentType;
 
   @Column(length = 500)
   private String fileName;
   
   @NotNull
   private long fileSize;
   
   @NotNull
   private String fileLanguage;
     
//   @ManyToOne
//   private User ownerUser;
//   
//   @ManyToOne
//   private Group ownerGroup;
   
   private BitSet permissions = new BitSet(9);
   
   public Inode(String filHash){
        this();
        this.fileHash = filHash;
    }
    
    public Inode(Inode other,Group group){
        this.fileHash = other.fileHash;
        this.edited = other.edited;
        this.fileContentType = other.fileContentType;
        this.fileLanguage = other.fileLanguage;
        this.fileSize = other.fileSize;
//        this.ownerUser = other.ownerUser;
        this.permissions = other.permissions;
//        this.ownerGroup = group;
    }
      
    public Inode() {

    }
   

    public BitSet getPermissions() {
        return permissions;
    }

    public void setPermissions(BitSet permissions) {
        this.permissions = permissions;
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

    public String getFileLanguage() {
        return fileLanguage;
    }

    public void setFileLanguage(String fileLanguage) {
        this.fileLanguage = fileLanguage;
    }

//    public User getOwnerUser() {
//        return ownerUser;
//    }
//
//    public void setOwnerUser(User ownerUser) {
//        this.ownerUser = ownerUser;
//    }
//
//    public Group getOwnerGroup() {
//        return ownerGroup;
//    }
//
//    public void setOwnerGroup(Group ownerGroup) {
//        this.ownerGroup = ownerGroup;
//    }

}
