/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;

import io.mutex.index.service.VirtualPageService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import javax.validation.constraints.NotNull;
import org.elasticsearch.client.Response;

/**
 *
 * @author Florent
 */
@Stateless
public class SearchVirtualPageService{

    private static final Logger LOG = Logger.getLogger(SearchVirtualPageService.class.getName());
    
    @Inject SearchHelper searchHelper;
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils envUtils;
    @Inject ElApiUtil elApiUtil;
    @Inject VirtualPageService virtualPageService;
    @Inject SearchLanguageService searchLanguageService;
    
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
        
        Set<Fragment> prefixPhraseFragments = matchPrefixPhraseQuery(groups, text);
        Set<Fragment> phraseFragments = matchPhrase(groups, text);
        
//        List<Fragment> phraseFragments = Collections.EMPTY_LIST;
        
        Set<Fragment> termFragments = match(groups,text);
        return prioritizeResult(text,prefixPhraseFragments, phraseFragments, termFragments);

    }
    
    private SortedSet<Fragment> prioritizeResult(String text, Set<Fragment> prefixPhraseFragments,
            Set<Fragment> phraseFragments, Set<Fragment> termFragments){
        
//        if(text.split("\\s+").length == 1){
            
            prefixPhraseFragments.retainAll(phraseFragments);
            prefixPhraseFragments.retainAll(termFragments);
            
            return prefixPhraseFragments.stream()
                    .collect(toCollection(TreeSet::new));
          
//            return Stream.of(termFragments,prefixPhraseFragments,phraseFragments)
//                   .flatMap(Set::stream)
//                   .collect(Collectors.toSet());
//       } 
//       return Stream.of(prefixPhraseFragments,phraseFragments,termFragments)
//                   .flatMap(Set::stream)
//                   .collect(Collectors.toSet());
    }
    
    private Set<Fragment> matchPrefixPhraseQuery(@NotNull List<Group> groups,String text){
       
        Optional<SearchRequest> oSearchReuest = 
                searchHelper.searchRequestBuilder(groups, text, prefixPhraseQueryBuilder);

        Optional<SearchResponse> oResponse = oSearchReuest.flatMap(sr -> searchHelper.search(sr));
        
        return oResponse.map(r -> searchHelper.extractFragments(r,AlgoPriority.PREFIX_PHRASE_MATCH))
                .orElseGet(() -> Collections.EMPTY_SET);
    }
    
    private Set<Fragment> matchPhrase(@NotNull List<Group> groups,String text){
       
        Optional<SearchRequest> oSearchReuest = 
                searchHelper.searchRequestBuilder(groups, text, phraseQueryBuilder);

        Optional<SearchResponse> oResponse = oSearchReuest.flatMap(sr -> searchHelper.search(sr));
        
        return oResponse.map(r -> searchHelper.extractFragments(r,AlgoPriority.PHRASE_MATCH))
                .orElseGet(() -> Collections.EMPTY_SET);
    }
      
    private Set<Fragment> match(List<Group> groups,String text){
      
        Optional<SearchRequest> oSearchReuest = 
                searchHelper.searchRequestBuilder(groups, text, matchQueryBuilder);
        
        Optional<SearchResponse> oResponse = oSearchReuest.flatMap(sr -> searchHelper.search(sr));  
        return oResponse.map(r -> searchHelper.extractFragments(r,AlgoPriority.PHRASE_MATCH))
                .orElseGet(() -> Collections.EMPTY_SET);
    }
    
//    private Set<Fragment> retrieveFragments(SearchResponse response,AlgoPriority algoPriority){
//        return searchHelper.extractFragments(response,algoPriority);
//    }
    
    private final Function<String,QueryBuilder> prefixPhraseQueryBuilder = (String text) -> {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhrasePrefixQuery(searchHelper.contentMappingProperty(), text));
    };
    
    private final Function<String,QueryBuilder> phraseQueryBuilder = (String text) -> {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery(searchHelper.contentMappingProperty(), text));
    };
         
    private final Function<String,QueryBuilder> matchQueryBuilder = (String text) -> {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(searchHelper.contentMappingProperty(), text));
    };
   
}
