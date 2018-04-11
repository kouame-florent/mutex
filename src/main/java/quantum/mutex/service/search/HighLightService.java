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
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import quantum.mutex.domain.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class HighLightService {
    
    @PersistenceContext
    EntityManager em;
    
    public List<VirtualPage> highLight(List<VirtualPage> rawResults, Analyzer analyzer,
            String fieldName, org.apache.lucene.search.Query luceneQuery){
        
        List<VirtualPage> results = new ArrayList<>();
   
        rawResults.forEach(vp -> {  
            try {
                TokenStream tokenStream = analyzer.tokenStream(fieldName, vp.getContent());
                QueryScorer scorer = new QueryScorer(luceneQuery, fieldName);
                Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
                Highlighter highlighter = new Highlighter(scorer);
                highlighter.setTextFragmenter(fragmenter);
                String highlightedText = highlighter.getBestFragments(tokenStream, fieldName, 4, "...");
                vp.setContent(highlightedText);
                results.add(vp);
            } catch (IOException | InvalidTokenOffsetsException ex) {
                Logger.getLogger(HighLightService.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return results;
        
    }
    
}
