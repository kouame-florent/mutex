/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.util.List;
import javax.ejb.Stateless;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import quantum.mutex.domain.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class QueryService {
    
    public List<VirtualPage> phraseQueryFrench(String searchText,
            FullTextEntityManager fullTextEntityManager){
         
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
            .buildQueryBuilder().forEntity(VirtualPage.class).get();
        
        org.apache.lucene.search.Query query = queryBuilder
            .keyword()
            .onFields("content_french","file.fileName")
            .matching(searchText)
            .createQuery();

        javax.persistence.Query persistenceQuery =
            fullTextEntityManager.createFullTextQuery(query, VirtualPage.class);
        persistenceQuery.setMaxResults(10);

        return persistenceQuery.getResultList();
    }
}
