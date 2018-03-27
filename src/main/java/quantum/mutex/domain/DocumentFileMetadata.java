/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Florent
 */
@Table(
    name = "document-file_metadata",
    uniqueConstraints = 
        @UniqueConstraint(columnNames = {"document_id","metadata_id"})
)
@Entity
public class DocumentFileMetadata extends RootEntity{
    
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
    
    @ManyToOne
    @JoinColumn(name = "metadata_id")
    private FileMetadata metadata;

    public DocumentFileMetadata() {
    }

    public DocumentFileMetadata(Document document, FileMetadata metadata) {
        this.document = document;
        this.metadata = metadata;
    }
    
    

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public FileMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FileMetadata metadata) {
        this.metadata = metadata;
    }
    
    
    
}
