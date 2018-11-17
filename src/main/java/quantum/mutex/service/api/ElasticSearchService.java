/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;



import com.google.gson.JsonObject;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import quantum.functional.api.Result;
import quantum.mutex.service.search.HighLightService;
import quantum.mutex.service.search.QueryService;

/**
 *
 * @author Florent
 */
@Stateless
public class ElasticSearchService {

    private static final Logger LOG = Logger.getLogger(ElasticSearchService.class.getName());
    
    @PersistenceUnit(unitName = "mutexPU")
    EntityManagerFactory emf;
    
    @Inject QueryService queryService;
    @Inject HighLightService highLightService;
    
    public final static String ELASTIC_SEARCH_SERVER_URI = "http://localhost:9200/";
    
  
    
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
