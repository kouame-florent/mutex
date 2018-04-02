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
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.GenericGenerator;


/**
 *
 * @author Florent
 */
@Table(
    name = "document_file-metadata"
)
@Entity
public class DocumentFileMetadata implements Serializable  {
    
    
    @Embeddable
    public static class Id implements Serializable{
        
        
        @Column(name = "document_id",columnDefinition = "BINARY(16)")
        private UUID documentId;
        
        @Column(name = "file_metadata_id",columnDefinition = "BINARY(16)")
        private UUID fileMetadataId;

        public Id() {
        }

        public Id(DocumentFile document, DocumentMetadata fileMetadata) {
            this.documentId = document.getUuid();
            this.fileMetadataId = fileMetadata.getUuid();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.documentId);
            hash = 59 * hash + Objects.hashCode(this.fileMetadataId);
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
            if (!Objects.equals(this.documentId, other.documentId)) {
                return false;
            }
            if (!Objects.equals(this.fileMetadataId, other.fileMetadataId)) {
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
    @JoinColumn(name = "document_id",updatable = false,insertable = false,referencedColumnName = "uuid")
    private DocumentFile document;
    
    @ManyToOne
    @JoinColumn(name = "file_metadata_id",updatable = false,insertable = false,referencedColumnName = "uuid")
    private DocumentMetadata fileMetadata;

    public DocumentFileMetadata() {
    }

    public DocumentFileMetadata(DocumentFile document, DocumentMetadata fileMetadata) {
        
        this.id = new Id(document, fileMetadata);
        
        this.document = document;
        this.fileMetadata = fileMetadata;
    }
    
    

    public DocumentFile getDocument() {
        return document;
    }

    public void setDocument(DocumentFile document) {
        this.document = document;
    }

    public DocumentMetadata getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(DocumentMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
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
        final DocumentFileMetadata other = (DocumentFileMetadata) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    
    
}
