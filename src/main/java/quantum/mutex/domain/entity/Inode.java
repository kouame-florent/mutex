/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.entity;


import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.shared.domain.BaseEntity;
import quantum.mutex.user.domain.entity.User;
import java.util.BitSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


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
@Getter @Setter
public class Inode extends BaseEntity{
  
//   
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
   
//   @ManyToOne
//   private Group ownerGroup;
   
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
   
}
