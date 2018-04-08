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
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.TermVector;


/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "VirtualPage.findByDocument",
        query = "SELECT v FROM VirtualPage v WHERE v.file = :file " 
    ),
   
})
@Indexed
@Table(name = "virtual_page")
@Entity
public class VirtualPage extends RootEntity{
    
    private int index;
    
    @Lob
    @Column(length = 50000)
    @Fields({
        @Field(name = "content_french",termVector = TermVector.WITH_POSITION_OFFSETS,
                analyzer = @Analyzer(definition = "french")),
        @Field(name = "content_ngram",
                analyzer = @Analyzer(definition = "ngram"))
    })
    private String content;
    
    @IndexedEmbedded
    @ManyToOne
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
