/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.QueryBuilder;
import quantum.mutex.domain.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class QueryService {

    private static final Logger LOG = Logger.getLogger(QueryService.class.getName());
    
       
    @Inject HighLightService highLightService;
    
    public List<VirtualPage> frenchPhraseQuery(String searchText, FullTextEntityManager fullTextEntityManager){
         QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query = queryBuilder
                             .phrase()
                             .onField("content_french")
                             .sentence(searchText)
                             .createQuery();
    
        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        fullTextQuery.setMaxResults(50);
        
        List<VirtualPage> rawResults = fullTextQuery.getResultList();
        LOG.log(Level.INFO, "-->> FRENCH PHARSE QUERY RAW RESULT SIZE: {0}", rawResults.size());
        Analyzer analyzer = retrieveAnalyser(fullTextEntityManager, "french");
        List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, 
                fullTextEntityManager, searchText,analyzer, query);

        return highLightedResults;
    }
    
    public List<VirtualPage> englishPhraseQuery(String searchText, FullTextEntityManager fullTextEntityManager){
         QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query = queryBuilder
                             .phrase()
                             .onField("content_english")
                             .sentence(searchText)
                             .createQuery();
    
        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        fullTextQuery.setMaxResults(50);
        
        List<VirtualPage> rawResults = fullTextQuery.getResultList();
        LOG.log(Level.INFO, "-->> ENGLISH PHARSE QUERY RAW RESULT SIZE: {0}", rawResults.size());
        Analyzer analyzer = retrieveAnalyser(fullTextEntityManager, "english");
        List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, 
                fullTextEntityManager, searchText,analyzer, query);

        return highLightedResults;
    }
    
    public List<VirtualPage> frenchKeyWordQuery(String searchText,FullTextEntityManager fullTextEntityManager){
         QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query =  queryBuilder
                             .keyword()
                             .onFields("content_french","mutexFile.fileName_french")
                             .matching(searchText)
                             .createQuery();
   
        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(50);
        
        List<VirtualPage> rawResults = persistenceQuery.getResultList();
        LOG.log(Level.INFO, "-->>FRENCH KEYWORD QUERY RAW RESULT SIZE: {0}", rawResults.size());
        Analyzer analyzer = retrieveAnalyser(fullTextEntityManager, "french");
        List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, 
                fullTextEntityManager, searchText,analyzer, query);

        return highLightedResults;
    }
    
     public List<VirtualPage> englishKeyWordQuery(String searchText,FullTextEntityManager fullTextEntityManager){
         QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query =  queryBuilder
                             .keyword()
                             .onFields("content_english","mutexFile.fileName_english")
                             .matching(searchText)
                             .createQuery();
   
        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(50);
        
        List<VirtualPage> rawResults = persistenceQuery.getResultList();
        LOG.log(Level.INFO, "-->>ENGLISH KEYWORD QUERY RAW RESULT SIZE: {0}", rawResults.size());
        Analyzer analyzer = retrieveAnalyser(fullTextEntityManager, "english");
        List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, 
                fullTextEntityManager, searchText,analyzer, query);

        return highLightedResults;
    }
    
    
    public List<VirtualPage> ngramQuery(String searchText,FullTextEntityManager fullTextEntityManager){
         
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query = queryBuilder
                         .keyword()
                         .onFields("contant_ngram")
                         .matching(searchText)
                         .createQuery();

        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(50);
        
        List<VirtualPage> rawResults = persistenceQuery.getResultList();
        LOG.log(Level.INFO, "-->>NGRAM QUERY RAW RESULT SIZE: {0}", rawResults.size());
//        rawResults.forEach(vp -> LOG.log(Level.INFO, "|||-- CONTENT LENGTH {0}", vp.getContent().length()));
        Analyzer analyzer = retrieveAnalyser(fullTextEntityManager, "ngram");
        List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, 
                fullTextEntityManager, searchText,analyzer, query);

        return highLightedResults;
    }
    
    private Analyzer retrieveAnalyser(FullTextEntityManager fmt,String name){
        SearchFactory searchFactory = fmt.getSearchFactory();
        return searchFactory.getAnalyzer(name);
    }
}
