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
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

/**
 *
 * @author Florent
 */
@Indexed
@Table(name = "mx_file")
@Entity
public class File extends BusinessEntity{
    
   @ElementCollection(targetClass = Permission.class,fetch = FetchType.EAGER)
   @CollectionTable(name = "mx_owner_Permission",joinColumns = @JoinColumn(name = "file_uuid"))
   @Column(name = "permission",nullable = false)
   @Enumerated(EnumType.STRING)
   private final Set<Permission> ownerPermissions = new HashSet<>();
   
   @ElementCollection(targetClass = Permission.class,fetch = FetchType.EAGER)
   @CollectionTable(name = "mx_group_permission",joinColumns = @JoinColumn(name = "file_uuid"))
   @Column(name = "permission",nullable = false)
   @Enumerated(EnumType.STRING)
   private final Set<Permission> groupPermissions = new HashSet<>();
   
   @ElementCollection(targetClass = Permission.class,fetch = FetchType.EAGER)
   @CollectionTable(name = "mx_other_permission",joinColumns = @JoinColumn(name = "file_uuid"))
   @Column(name = "permission",nullable = false)
   @Enumerated(EnumType.STRING)
   private final Set<Permission> otherPermissions = new HashSet<>();
   
   @NotNull
   @Column(length = 1000)
   private String fileHash;
   
   @NotNull
   private String fileContentType;
   
   @Fields({
        @Field(name="fileName_french", 
                analyzer=@Analyzer(definition = "french"), termVector = TermVector.WITH_POSITION_OFFSETS),
        @Field(name="fileName_english",
                analyzer =@Analyzer(definition = "english"), termVector = TermVector.WITH_POSITION_OFFSETS),
        @Field(name="fileName_ngram",
                analyzer =@Analyzer(definition = "ngram"),  termVector = TermVector.WITH_POSITION_OFFSETS)
    })
   @NotNull
   @Field
   @Column(length = 500)
   private String fileName;
   
   @NotNull
   private long fileSize;
   
   @NotNull
   private String fileLanguage;
   
   @OneToMany(mappedBy = "file")
   @ContainedIn
   private Set<VirtualPage> virtualPages;
   
   @ManyToOne
   private User ownerUser;
   
   @ManyToOne
   private Group ownerGroup;
   
    
    public File(String filHash){
        this();
        this.fileHash = filHash;
    }
      
    public File() {
        initAcces();
    }
   
    private void initAcces(){
        ownerPermissions.addAll(EnumSet.of(Permission.READ,Permission.WRITE));
        groupPermissions.addAll(EnumSet.of(Permission.READ));
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

    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    public Group getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(Group ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    
    
    
}
