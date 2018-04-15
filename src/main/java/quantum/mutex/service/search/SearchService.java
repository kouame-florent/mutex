/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.SynchronizationType;
import org.hibernate.search.jpa.FullTextEntityManager;
import quantum.mutex.domain.VirtualPage;
import quantum.mutex.view.VirtualPageSearchView;

/**
 *
 * @author Florent
 */
@Stateless
public class SearchService {

    private static final Logger LOG = Logger.getLogger(SearchService.class.getName());
    
    @PersistenceUnit(unitName = "mutexPU")
    EntityManagerFactory emf;
    
    @Inject QueryService queryService;
    @Inject HighLightService highLightService;
    
    @PostConstruct
    public void init(){
       
    }
    
    public List<VirtualPage> search(String searchText){
        EntityManager em = emf.createEntityManager(SynchronizationType.UNSYNCHRONIZED);
        
        FullTextEntityManager ftem =
                   org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
        
        List<VirtualPage> results = new ArrayList<>();
        
        List<VirtualPage> phraseQueryResults = queryService.phraseQuery(searchText, ftem);
        LOG.log(Level.INFO, "-->> PHRASE QUERY SIZE: {0}", phraseQueryResults.size());
        results.addAll(getDistinct(phraseQueryResults));
        
        if(results.size() < 50){
            em.clear();
            List<VirtualPage> keyWordQueryResults = queryService.keyWordQuery(searchText, ftem);
            LOG.log(Level.INFO, "-->> KEY WORD QUERY SIZE: {0}", keyWordQueryResults.size());
            results.addAll(getDistinct(keyWordQueryResults));
        }  
         
        em.close();
        return results;
       
    }
      
    
    private void applySearchAlgorithm(List<VirtualPage> virtualPages){
        if(virtualPages.size() < 50){
            
        }        
    }
    
    private List<VirtualPage> getDistinct(List<VirtualPage> virtualPages){
        return virtualPages.stream()
                .map(VirtualPageSearchView::new)
                .distinct()
                .map(vpv -> vpv.getVirtualPage())
                .collect(Collectors.toList());
    }
    
}
