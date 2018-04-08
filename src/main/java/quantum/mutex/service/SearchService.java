/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.SynchronizationType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import quantum.mutex.domain.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class SearchService {

    private static final Logger LOG = Logger.getLogger(SearchService.class.getName());
   
    @PersistenceUnit(unitName = "mutexPU")
    EntityManagerFactory emf;
    
    @PostConstruct
    public void init(){
       
    }
    
    public List<VirtualPage> search(String searchText){
        
        EntityManager em = emf.createEntityManager(SynchronizationType.UNSYNCHRONIZED);
        
        FullTextEntityManager fullTextEntityManager =
                   org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
        //IndexReader indexReader = searchFactory.getIndexReaderAccessor().open(VirtualPage.class);
        Analyzer frenchAnalyzer = searchFactory.getAnalyzer("french");

        
        String tokens = tokenizeStream(frenchAnalyzer, searchText);
        if(tokens.isEmpty()){
            return new ArrayList<>();
        }
        
        
        org.apache.lucene.search.Query query = queryBuilder
            .keyword()
            .onFields("content_french","file.fileName")
            .matching(searchText)
            .createQuery();

        // wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(10);
       

        // execute search
        return highLight(persistenceQuery.getResultList(), frenchAnalyzer, query);
    }
    
    
    private  List<VirtualPage> highLight(List<VirtualPage> rawResults,
                    Analyzer analyzer, org.apache.lucene.search.Query luceneQuery){
        
        List<VirtualPage> highlightedResults = new ArrayList<>();
        
        Highlighter highlighter = 
                new Highlighter(new SimpleHTMLFormatter("<b style='color: #13ad30'>", "</b>"),new 
                    QueryScorer(luceneQuery));
                    
        rawResults.forEach((vps) -> {
            try {
                String[] contentFragments = highlighter
                        .getBestFragments(analyzer, "content_french", vps.getContent(),3);
                LOG.log(Level.INFO, "-->>->> FRAGMENTS: {0}",contentFragments );
                
                vps.setContent(Arrays.stream(contentFragments).collect(Collectors.joining("...")));
                highlightedResults.add(vps);
            } catch (IOException | InvalidTokenOffsetsException ex) {
                Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        return highlightedResults;
    }
    
    private String tokenizeStream(Analyzer analyzer,String words){
        
        List<String> result = new ArrayList<>();
        
        try {
            
            try ( 
                TokenStream tokenStream = analyzer.tokenStream("libelle_french", words)) {
                tokenStream.reset();
                while(tokenStream.incrementToken()){
                    result.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
                }
                tokenStream.end();
            }

            
        } catch (IOException ex) {
            Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String value = result.stream().collect(Collectors.joining(" "));
        LOG.log(Level.INFO, "-->-->> FINAL TOKEN: {0}", value);
        
        return value;
    }
    
}
