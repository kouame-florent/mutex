/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;


/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "VirtualPage.findByDocument",
        query = "SELECT v FROM VirtualPage v WHERE v.document = :document " 
    ),
   
})
@Indexed
@Table(name = "virtual_page")
@Entity
public class VirtualPage extends RootEntity{
    
    private int index;
    
    @Lob
    @Column(length = 50000)
    @Field
    private String content;
    
    @IndexedEmbedded
    @ManyToOne
    private DocumentFile document;

    public DocumentFile getDocument() {
        return document;
    }

    public void setDocument(DocumentFile document) {
        this.document = document;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    
    
}
