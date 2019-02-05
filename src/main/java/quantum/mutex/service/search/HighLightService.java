/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.ejb.Stateless;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.search.highlight.Highlighter;
//import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
//import org.apache.lucene.search.highlight.QueryScorer;
//import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
//import quantum.mutex.domain.dto.VirtualPage;
//
///**
// *
// * @author Florent
// */
//@Stateless
//public class HighLightService {
//
//    private static final Logger LOG = Logger.getLogger(HighLightService.class.getName());
//    
//    
//    @PersistenceContext
//    EntityManager em;
//    
////    public List<VirtualPageDTO> highLight(List<VirtualPageDTO> rawResults, FullTextEntityManager fmt,
////            String fieldName,Analyzer analyzer, org.apache.lucene.search.Query luceneQuery){
////       
////        List<VirtualPageDTO> results = new ArrayList<>();
////   
////        rawResults.forEach(vp -> {  
////            Highlighter highlighter = getHighlighter(luceneQuery);
////            TokenStream tokenStream = getTokenStream(vp,fieldName,analyzer);
////            String highlightedText = getHighlightedText(highlighter, tokenStream, vp);
////            vp.setContent(highlightedText);
////            results.add(vp);
////        });
////        LOG.log(Level.INFO, "...> HIGHLIGHTED RESULT SIZE: {0}",results.size());
////        return results;
////    }  
//    
//    private Highlighter getHighlighter(org.apache.lucene.search.Query luceneQuery){
//        return new Highlighter(new SimpleHTMLFormatter("<b style='color: #32a851'>", "</b>"),
//              new QueryScorer(luceneQuery));
//    }
//    
//    private TokenStream getTokenStream(VirtualPage virtualPage,String fieldName,
//            Analyzer analyzer){
//        return analyzer.tokenStream(fieldName, virtualPage.getContent());
//    }
//    
//    private String getHighlightedText(Highlighter hi,TokenStream ts,VirtualPage vp){
//        try {
//            return hi.getBestFragments(ts, vp.getContent(), 5, "...");
//        } catch (IOException | InvalidTokenOffsetsException ex) {
//            Logger.getLogger(HighLightService.class.getName()).log(Level.SEVERE, null, ex);
//            return vp.getContent();
//        }
//    }
//
//    
//}
