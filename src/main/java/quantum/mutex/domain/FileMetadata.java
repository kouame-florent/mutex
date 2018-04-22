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
@Table(
    name = "file_metadata"
)
@Entity
public class FileMetadata implements Serializable  {
     
    @Embeddable
    public static class Id implements Serializable{
        
        @Column(name = "file_id",columnDefinition = "BINARY(16)")
        private UUID fileId;
        
        @Column(name = "metadata_id",columnDefinition = "BINARY(16)")
        private UUID metadataId;

        public Id() {
        }

        public Id(File file, quantum.mutex.domain.Metadata metadata) {
            this.fileId = file.getUuid();
            this.metadataId = metadata.getUuid();
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + Objects.hashCode(this.fileId);
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
            if (!Objects.equals(this.fileId, other.fileId)) {
                return false;
            }
            if (!Objects.equals(this.metadataId, other.metadataId)) {
                return false;
            }
            return true;
        }

        

    
    }
    
    @Version
    protected long version;
    
    @EmbeddedId
    protected Id id = new Id();
    
    @ManyToOne
    @JoinColumn(name = "file_id",updatable = false,insertable = false,referencedColumnName = "uuid")
    private File file;
    
    @ManyToOne
    @JoinColumn(name = "metadata_id",updatable = false,insertable = false,referencedColumnName = "uuid")
    private Metadata metadata;

    public FileMetadata() {
    }

    public FileMetadata(File file, Metadata metadata) {
        
        this.id = new Id(file, metadata);
        
        this.file = file;
        this.metadata = metadata;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    
   

    public Id getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.id);
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
        final FileMetadata other = (FileMetadata) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    
    
}
