/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.SynchronizationType;
import quantum.mutex.common.Function;
import quantum.mutex.common.Tuple;
import quantum.mutex.dto.VirtualPageDTO;
import quantum.mutex.util.Constants;
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
    
    
//    public List<VirtualPageDTO> search(String searchText){
//        EntityManager em = emf.createEntityManager(SynchronizationType.UNSYNCHRONIZED);
//        
//        FullTextEntityManager ftem =
//                   org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
//        
//        Tuple<List<VirtualPageDTO>,Boolean> withFrPhrase = processWithFrenchPhrase.apply(searchText).apply(ftem);
//        
//        Tuple<List<VirtualPageDTO>,Boolean> withEnPhrase = (withFrPhrase._2) ? 
//                new Tuple<>(Collections.EMPTY_LIST,Boolean.FALSE) :  processWithEnglishPhrase.apply(searchText).apply(ftem);
//        
//        Tuple<List<VirtualPageDTO>,Boolean> withFrKeyWord = (withEnPhrase._2) ? 
//                new Tuple<>(Collections.EMPTY_LIST,Boolean.FALSE) :  processWithFrenchKeyWord.apply(searchText).apply(ftem);
//        
//        Tuple<List<VirtualPageDTO>,Boolean> withEnKeyWord = (withFrKeyWord._2) ? 
//                new Tuple<>(Collections.EMPTY_LIST,Boolean.FALSE) :  processWithEnglishKeyWord.apply(searchText).apply(ftem);
//        
//        Tuple<List<VirtualPageDTO>,Boolean> withNgram = (withEnKeyWord._2) ? 
//                 new Tuple<>(Collections.EMPTY_LIST,Boolean.FALSE) : processWithNgram.apply(searchText).apply(ftem);
//        
//        em.close();
//        
//        List<VirtualPageDTO> pages = Stream.of(withFrPhrase._1,withEnPhrase._1, 
//                withFrKeyWord._1,withEnKeyWord._1,withNgram._1)
//                .flatMap(List::stream).collect(Collectors.toList());
//        
//        return produceDistinct(pages);
//          
//        
//    }
//    
//    private List<VirtualPageDTO> produceDistinct(List<VirtualPageDTO> vps){
//       return io.vavr.collection.List.ofAll(vps)
//                    .distinctBy(VirtualPageDTO::getMutexFileUUID).toJavaList();
//    }
//    
//    private final Function<String,Function<FullTextEntityManager,Tuple<List<VirtualPageDTO>,Boolean>>> processWithFrenchPhrase = s -> ftem -> {
//        ftem.clear();
//        List<VirtualPageDTO> res = this.distinct.apply(queryService.frenchPhraseQuery(s,ftem));
//        boolean stop =  (res.size() - Constants.SEARCH_RESULT_THRESHOLD) >= 0;
//        return new Tuple<>(res,stop );
//    };
//    
//    private final Function<String,Function<FullTextEntityManager,Tuple<List<VirtualPageDTO>,Boolean>>> processWithEnglishPhrase = s -> ftem -> {
//        ftem.clear();
//        List<VirtualPageDTO> res = this.distinct.apply(queryService.englishPhraseQuery(s,ftem));
//        boolean stop =  (res.size() - Constants.SEARCH_RESULT_THRESHOLD) >= 0;
//        return new Tuple<>(res,stop );
//    };
//    
//    private final Function<String, Function<FullTextEntityManager, Tuple<List<VirtualPageDTO>,Boolean>> > processWithFrenchKeyWord = s -> ftem -> {
//        ftem.clear();
//        List<VirtualPageDTO> res = this.distinct.apply(queryService.frenchKeyWordQuery(s,ftem));
//        boolean stop =  (res.size() - Constants.SEARCH_RESULT_THRESHOLD) >= 0;
//        return new Tuple<>(res,stop );
//    };
//    
//     private final Function<String, Function<FullTextEntityManager, Tuple<List<VirtualPageDTO>,Boolean>> > processWithEnglishKeyWord = s -> ftem -> {
//        ftem.clear();
//        List<VirtualPageDTO> res = this.distinct.apply(queryService.englishKeyWordQuery(s,ftem));
//        boolean stop =  (res.size() - Constants.SEARCH_RESULT_THRESHOLD) >= 0;
//        return new Tuple<>(res,stop );
//    };
//    
//    private final Function<String, Function<FullTextEntityManager, Tuple<List<VirtualPageDTO>,Boolean>> > processWithNgram = s -> ftem -> {
//        ftem.clear();
//        List<VirtualPageDTO> res = this.distinct.apply(queryService.ngramQuery(s, ftem));
//        boolean stop =  (res.size() - Constants.SEARCH_RESULT_THRESHOLD) >= 0;
//        return new Tuple<>(res,stop );
//    };
//    
//  
//    private final Function<List<VirtualPageDTO>,List<VirtualPageDTO>> distinct =  vp -> {
//        return vp.stream().map(VirtualPageSearchView::new).distinct()
//                .map(vpv -> vpv.getVirtualPage())
//                .collect(Collectors.toList());
//    };
//    

}
