/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template mutexFile, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;


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
        name = "VirtualPage.findByMutexFile",
        query = "SELECT v FROM VirtualPage v WHERE v.mutexFile = :mutexFile " 
    ),
   
})
@Indexed
@Table(name = "mx_virtual_page")
@Entity
public class VirtualPage extends BusinessEntity{
    
    private int pageIndex;
    
    @Fields({
        @Field(name="content_french", 
                analyzer=@Analyzer(definition = "french"), termVector = TermVector.WITH_POSITION_OFFSETS),
        @Field(name="content_english",
                analyzer =@Analyzer(definition = "english"), termVector = TermVector.WITH_POSITION_OFFSETS),
        @Field(name="contant_ngram",
                analyzer =@Analyzer(definition = "ngram"),  termVector = TermVector.WITH_POSITION_OFFSETS)
    })
    @Lob
//    @Column(length = 50000)
    @Field
    private String content;
    
    @IndexedEmbedded
    @ManyToOne
    private MutexFile mutexFile;
    
    public VirtualPage() {
    }
    
    public VirtualPage(String content) {
        this.content = content;
    }

    public VirtualPage(int pageIndex, String content) {
        this.pageIndex = pageIndex;
        this.content = content;
    }

    public MutexFile getMutexFile() {
        return mutexFile;
    }

    public void setMutexFile(MutexFile mutexFile) {
        this.mutexFile = mutexFile;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    
    
}
