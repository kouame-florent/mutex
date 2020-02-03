/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.mutex.search.valueobject.Fragment;
import io.mutex.user.entity.Group;
import io.mutex.index.service.ElApiUtil;
import io.mutex.search.valueobject.AlgoPriority;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.user.service.UserGroupService;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import static java.util.stream.Collectors.toCollection;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Florent
 */
@Stateless
public class VirtualPageService{

    private static final Logger LOG = Logger.getLogger(VirtualPageService.class.getName());
    
    @Inject Helper helper;
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils envUtils;
    @Inject ElApiUtil elApiUtil;
    @Inject VirtualPageService virtualPageService;
    @Inject LanguageService searchLanguageService;
    
    @Resource(name = "DefaultManagedExecutorService")
    private ManagedExecutorService executor;
    
    public SortedSet<Fragment> search(@NotNull List<Group> selectedGroups,String text){
        LOG.log(Level.INFO, "--> SELECTED GROUP : {0}", selectedGroups);  
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> processSearchStack(gps,text))
                    .orElseGet(() -> Collections.emptySortedSet());
        }else{
            return processSearchStack(selectedGroups,text); 
        }
    }
    
    private SortedSet<Fragment> processSearchStack(@NotNull List<Group> groups,String text){
//        
//        Set<Fragment> prefixPhraseFragments = matchPrefixPhrase(groups, text);
//        Set<Fragment> phraseFragments = matchPhrase(groups, text);
//        Set<Fragment> termFragments = match(groups,text);
//        
        CompletableFuture<Set<Fragment>> ppFrags = 
                CompletableFuture.supplyAsync(() -> matchPrefixPhrase(groups, text), executor);
        CompletableFuture<Set<Fragment>> pFrags = 
                CompletableFuture.supplyAsync(() -> matchPhrase(groups, text), executor);
        CompletableFuture<Set<Fragment>> mFrags = 
                CompletableFuture.supplyAsync(() -> match(groups, text), executor);
        
        
        
        return mergeResults(Set.of(ppFrags, pFrags, mFrags));
        
//        return mergeResults(prefixPhraseFragments, phraseFragments, termFragments);

    }
    
     private Set<Fragment> matchPrefixPhrase(@NotNull List<Group> groups,String text){
       
        Optional<SearchRequest> oSearchReuest = 
                helper.searchRequestBuilder(groups, text, prefixPhraseQueryBuilder);

        Optional<SearchResponse> oResponse = oSearchReuest.flatMap(sr -> helper.search(sr));
        
        return oResponse.map(r -> helper.extractFragments(r,AlgoPriority.PREFIX_PHRASE_MATCH))
                .orElseGet(() -> Collections.EMPTY_SET);
    }
         
    private Set<Fragment> matchPhrase(@NotNull List<Group> groups,String text){
       
        Optional<SearchRequest> oSearchReuest = 
                helper.searchRequestBuilder(groups, text, phraseQueryBuilder);

        Optional<SearchResponse> oResponse = oSearchReuest.flatMap(sr -> helper.search(sr));
        
        return oResponse.map(r -> helper.extractFragments(r,AlgoPriority.PHRASE_MATCH))
                .orElseGet(() -> Collections.EMPTY_SET);
    }
            
    private Set<Fragment> match(List<Group> groups,String text){
      
        Optional<SearchRequest> oSearchReuest = 
                helper.searchRequestBuilder(groups, text, matchQueryBuilder);
        
        Optional<SearchResponse> oResponse = oSearchReuest.flatMap(sr -> helper.search(sr));  
        return oResponse.map(r -> helper.extractFragments(r,AlgoPriority.MATCH))
                .orElseGet(() -> Collections.EMPTY_SET);
    }
    
    private SortedSet<Fragment> mergeResults(Set<Fragment> prefixPhraseFragments,
            Set<Fragment> phraseFragments, Set<Fragment> termFragments){
 
            Set<Fragment> fragments = new HashSet<>();
            fragments.addAll(prefixPhraseFragments);
            fragments.addAll(phraseFragments);
            fragments.addAll(termFragments);
                    
            return fragments.stream()
                    .collect(toCollection(TreeSet::new)).descendingSet();
  
    }
   
    private SortedSet<Fragment> mergeResults(Set<CompletableFuture<Set<Fragment>>> futureFragments){
        return futureFragments.stream()
                .map(CompletableFuture::join)
                .flatMap(Set::stream)
                .collect(toCollection(TreeSet::new )).descendingSet();
    }
  
    
//    private Set<Fragment> retrieveFragments(SearchResponse response,AlgoPriority algoPriority){
//        return searchHelper.extractFragments(response,algoPriority);
//    }
    
    private final Function<String,QueryBuilder> prefixPhraseQueryBuilder = (String text) -> {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhrasePrefixQuery(helper.contentMappingProperty(), text));
    };
    
    private final Function<String,QueryBuilder> phraseQueryBuilder = (String text) -> {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(helper.contentMappingProperty(), text));
    };
         
    private final Function<String,QueryBuilder> matchQueryBuilder = (String text) -> {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(helper.contentMappingProperty(), text));
    };
   
}
