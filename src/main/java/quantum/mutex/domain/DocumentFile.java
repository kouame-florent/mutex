/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 *
 * @author Florent
 */
@Indexed
@Table(name = "document_file")
@Entity
public class DocumentFile extends RootEntity{
    
   @ElementCollection(targetClass = Permission.class)
   @CollectionTable(name = "owner_Permission",joinColumns = @JoinColumn(name = "document_uuid"))
   @Column(name = "permission",nullable = false)
   @Enumerated(EnumType.STRING)
   private final Set<Permission> ownerPermissions = new HashSet<>();
   
   @ElementCollection(targetClass = Permission.class)
   @CollectionTable(name = "group_permission",joinColumns = @JoinColumn(name = "document_uuid"))
   @Column(name = "permission",nullable = false)
   @Enumerated(EnumType.STRING)
   private final Set<Permission> groupPermissions = new HashSet<>();
   
   @ElementCollection(targetClass = Permission.class)
   @CollectionTable(name = "other_permission",joinColumns = @JoinColumn(name = "document_uuid"))
   @Column(name = "permission",nullable = false)
   @Enumerated(EnumType.STRING)
   private final Set<Permission> otherPermissions = new HashSet<>();
   
   
   private String fileHash;
   private String fileContentType;
   
   @Field
   private String fileName;
   
   private long fileSize;
   private String fileLanguage;
   
   @OneToMany(mappedBy = "document")
   @ContainedIn
   private Set<VirtualPage> virtualPages;
   
    
    public DocumentFile(String filHash){
        this();
        this.fileHash = filHash;
    }
      
    public DocumentFile() {
        initAcces();
    }
   
    private void initAcces(){
        ownerPermissions.addAll(EnumSet.of( Permission.READ,Permission.EXECUTE));
    }

    public Set<Permission> getOwnerPermissions() {
        return ownerPermissions;
    }

    public Set<Permission> getGroupPermissions() {
        return groupPermissions;
    }

    public Set<Permission> getOtherPermissions() {
        return otherPermissions;
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

    public Set<VirtualPage> getVirtualPages() {
        return virtualPages;
    }

    public void setVirtualPages(Set<VirtualPage> virtualPages) {
        this.virtualPages = virtualPages;
    }

    
    
}
