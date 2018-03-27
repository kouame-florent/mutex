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
import javax.persistence.Table;

/**
 *
 * @author Florent
 */
@Table(name = "document")
@Entity
public class Document extends RootEntity{
    
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
    
    public Document(String filHash){
        this();
        this.fileHash = filHash;
    }
      
    public Document() {
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

    
    
}
