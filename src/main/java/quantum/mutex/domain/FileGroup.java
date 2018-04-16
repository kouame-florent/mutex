/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 *
 * @author Florent
 */
@Table(name = "fle_group")
@Entity
public class FileGroup implements Serializable {
    
    @Embeddable
    public static class Id implements Serializable{
        
        @Column(name = "user_uuid",columnDefinition = "BINARY(16)")
        private UUID fleId;

        @Column(name = "group_uuid",columnDefinition = "BINARY(16)")
        private UUID groupId;
         
        public Id(){}
         
        public Id(File file, Group group){
             this.fleId = file.getUuid();
             this.groupId = group.getUuid();
         }

        public UUID getFleId() {
            return fleId;
        }

        public void setFleId(UUID fleId) {
            this.fleId = fleId;
        }

        

        public UUID getGroupId() {
            return groupId;
        }

        public void setGroupId(UUID groupId) {
            this.groupId = groupId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + Objects.hashCode(this.fleId);
            hash = 31 * hash + Objects.hashCode(this.groupId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Id other = (Id) obj;
            if (!Objects.equals(this.fleId, other.fleId)) {
                return false;
            }
            if (!Objects.equals(this.groupId, other.groupId)) {
                return false;
            }
            return true;
        }
   }
      
    @EmbeddedId
    protected Id id = new Id();
    
    @Version
    protected long version;
    
    @ManyToOne
    @JoinColumn(name = "file_uuid",insertable = false,updatable = false)
    private File file;
    
    @ManyToOne
    @JoinColumn(name = "group_uuid",insertable = false,updatable = false)
    private Group group;

    public FileGroup() {
    }

    public FileGroup(File file, Group group) {
        
        this.id = new Id(file, group);
        this.file = file;
        this.group = group;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.file);
        hash = 37 * hash + Objects.hashCode(this.group);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileGroup other = (FileGroup) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        if (!Objects.equals(this.group, other.group)) {
            return false;
        }
        return true;
    }
  
    
}
