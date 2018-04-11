/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.SynchronizationType;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import quantum.mutex.domain.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class SearchService {
    
    @PersistenceUnit(unitName = "mutexPU")
    EntityManagerFactory emf;
    
    @Inject QueryService queryService;
    
    
    @PostConstruct
    public void init(){
       
    }
    
    public List<VirtualPage> search(String searchText){
        EntityManager em = emf.createEntityManager(SynchronizationType.UNSYNCHRONIZED);
        
        FullTextEntityManager ftem =
                   org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        
        List<VirtualPage> phraseQueryFr = queryService.phraseQueryFrench(searchText, ftem);
        
        List<VirtualPage> virtualPages = new ArrayList<>();
        virtualPages.addAll(phraseQueryFr);
        
        em.close();
        return virtualPages;
    }
    
}
