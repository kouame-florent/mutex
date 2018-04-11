/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.hibernate.id.enhanced.HiLoOptimizer;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import quantum.mutex.domain.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class QueryService {
    
    
    @Inject HighLightService highLightService;
    
    public List<VirtualPage> phraseQueryFrench(String searchText,
            FullTextEntityManager fullTextEntityManager){
         
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query = queryBuilder
            .phrase()
            .onField("content_french")
            .sentence(searchText)
            .createQuery();

        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(10);

        return persistenceQuery.getResultList();
    }
    
    public List<VirtualPage> keyWordQueryFrench(String searchText,
            FullTextEntityManager fullTextEntityManager){
         
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query = queryBuilder
            .keyword()
            .onFields("content_french","file.fileName_french")
            .matching(searchText)
            .createQuery();

        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(10);

        return persistenceQuery.getResultList();
    }
}
