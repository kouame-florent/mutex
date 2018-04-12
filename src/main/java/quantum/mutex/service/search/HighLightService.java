/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import quantum.mutex.domain.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class HighLightService {

    private static final Logger LOG = Logger.getLogger(HighLightService.class.getName());
    
    
    @PersistenceContext
    EntityManager em;
    
    public List<VirtualPage> highLight(List<VirtualPage> rawResults, Analyzer analyzer,
            String fieldName, org.apache.lucene.search.Query luceneQuery){
        
        LOG.log(Level.SEVERE, "... HIGHLIGHTING ...");
        List<VirtualPage> results = new ArrayList<>();
   
        rawResults.forEach(vp -> {  
            try {
                Highlighter highlighter = 
                new Highlighter(new SimpleHTMLFormatter("<b style='color: #32a851'>", "</b>"),new 
                    QueryScorer(luceneQuery));
                TokenStream tokenStream = analyzer.tokenStream(fieldName, vp.getContent());
                String highlightedText = highlighter.getBestFragments(tokenStream, vp.getContent(), 4, "...");
                //LOG.log(Level.SEVERE, "-->> BEST FRAGMENT: {0}", highlightedText);
                vp.setContent(highlightedText);
                results.add(vp);
            } catch (IOException | InvalidTokenOffsetsException ex) {
                Logger.getLogger(HighLightService.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return results;
        
    }
    
    public List<VirtualPage> highLight(List<VirtualPage> rawResults, FullTextEntityManager fmt,
            String fieldName, org.apache.lucene.search.Query luceneQuery){
        
        LOG.log(Level.SEVERE, "... HIGHLIGHTING ...");
        List<VirtualPage> results = new ArrayList<>();
   
        rawResults.forEach(vp -> {  
            try {
                Highlighter highlighter = 
                new Highlighter(new SimpleHTMLFormatter("<b style='color: #32a851'>", "</b>"),new 
                    QueryScorer(luceneQuery));
                TokenStream tokenStream = getAnalyzer(vp, fmt).tokenStream(fieldName, vp.getContent());
                String highlightedText = highlighter.getBestFragments(tokenStream, vp.getContent(), 4, "...");
                //LOG.log(Level.SEVERE, "-->> BEST FRAGMENT: {0}", highlightedText);
                vp.setContent(highlightedText);
                results.add(vp);
            } catch (IOException | InvalidTokenOffsetsException ex) {
                Logger.getLogger(HighLightService.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return results;
    }
    
    private Analyzer getAnalyzer(VirtualPage virtualPage,FullTextEntityManager fmt){
        SearchFactory searchFactory = fmt.getSearchFactory();
        if(virtualPage.getFile().getFileLanguage().equals("en")){
            return searchFactory.getAnalyzer("english");
        }
        return searchFactory.getAnalyzer("french");
       
    }
    
}
