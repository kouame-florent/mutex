/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.util.ArrayList;
import java.util.List;
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
        
        //List<VirtualPage> results = new ArrayList<>();
        
        List<VirtualPage> phraseQueryResults = queryService.phraseQuery(searchText, ftem);
     //   List<VirtualPage> phraseQueryResEn = queryService.phraseQueryEnglish(searchText, ftem);
        
     //   List<VirtualPage> results = mergeAndSort(phraseQueryResFr, phraseQueryResEn);
        
//        List<VirtualPage> virtualPages = new ArrayList<>();
//        virtualPages.addAll(phraseQueryResFr);

       // highLightService.highLight(results, ftem, searchText, luceneQuery);
       
        em.close();
        return getDistinct(phraseQueryResults);
       // return mergeAndSort(phraseQueryResFr, phraseQueryResEn);
    }
    
    private List<VirtualPage> getDistinct(List<VirtualPage> virtualPages){
        
        return virtualPages.stream()
                .map(VirtualPageSearchView::new)
                .distinct()
                .map(vpv -> vpv.getVirtualPage())
                .collect(Collectors.toList());
        
    }
    
}
