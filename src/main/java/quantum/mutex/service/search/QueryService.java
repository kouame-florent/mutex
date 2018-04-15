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
    
    public List<VirtualPage> phraseQuery(String searchText,
            FullTextEntityManager fullTextEntityManager){
         
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
      //  Analyzer analyzer = searchFactory.getAnalyzer("french");
        
        org.apache.lucene.search.Query query = queryBuilder.bool()
               .should(queryBuilder
                        .phrase()
                        .onField("content_french")
                        .sentence(searchText)
                        .createQuery() )
                .should(queryBuilder
                        .phrase()
                        .onField("content_english")
                        .sentence(searchText)
                        .createQuery() )
                .createQuery();

//        org.apache.lucene.search.Query query = queryBuilder
//            .phrase()
//            .onField("content_french")
//            .sentence(searchText)
//            .createQuery();
        
        

//        javax.persistence.Query persistenceQuery =
//            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
//        persistenceQuery.setMaxResults(50);
//        
        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        fullTextQuery.setMaxResults(50);
        
        List<VirtualPage> rawResults = fullTextQuery.getResultList();
                
       // List<VirtualPage> rawResults = persistenceQuery.getResultList();
       // LOG.log(Level.INFO, "-->> RAW RESULT SIZE: {0}", rawResults.size());
       // List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, analyzer, searchText, query);
       List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, fullTextEntityManager, searchText, query);

        return highLightedResults;
    }
    
    public List<VirtualPage> phraseQueryEnglish(String searchText,
            FullTextEntityManager fullTextEntityManager){
         
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
        Analyzer analyzer = searchFactory.getAnalyzer("english");
        
        org.apache.lucene.search.Query query = queryBuilder
            .phrase()
            .onField("content_english")
            .sentence(searchText)
            .createQuery();

        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(100);
        
        List<VirtualPage> rawResults = persistenceQuery.getResultList();
        LOG.log(Level.INFO, "-->> RAW RESULT SIZE: {0}", rawResults.size());
        List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, analyzer, searchText, query);

        return highLightedResults;
    }
    
    public List<VirtualPage> keyWordQuery(String searchText,
            FullTextEntityManager fullTextEntityManager){
         
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query = queryBuilder.bool()
               .should(queryBuilder
                        .keyword()
                        .onFields("content_french","file.fileName_french")
                        .matching(searchText)
                        .createQuery() )
                .should(queryBuilder
                        .keyword()
                        .onFields("content_english","file.fileName_english")
                        .matching(searchText)
                        .createQuery() )
                .createQuery();
        
        
        
//        org.apache.lucene.search.Query query = queryBuilder
//            .keyword()
//            .onFields("content_french","file.fileName_french")
//            .matching(searchText)
//            .createQuery();

        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(50);
        
        List<VirtualPage> rawResults = persistenceQuery.getResultList();
        LOG.log(Level.INFO, "-->> RAW RESULT SIZE: {0}", rawResults.size());
        // List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, analyzer, searchText, query);
        List<VirtualPage> highLightedResults = highLightService.highLight(rawResults, fullTextEntityManager, searchText, query);


        return persistenceQuery.getResultList();
    }
    
     public List<VirtualPage> keyWordQueryEnglish(String searchText,
            FullTextEntityManager fullTextEntityManager){
         
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query = queryBuilder
            .keyword()
            .onFields("content_english","file.fileName_english")
            .matching(searchText)
            .createQuery();

        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(100);

        return persistenceQuery.getResultList();
    }
}
