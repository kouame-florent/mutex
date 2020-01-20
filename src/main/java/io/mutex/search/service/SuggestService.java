/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;


import io.mutex.user.service.impl.UserGroupServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import io.mutex.search.valueobject.MutexCompletionSuggestion;
import io.mutex.search.valueobject.MutexPhraseSuggestion;
import io.mutex.search.valueobject.MutexTermSuggestion;
import io.mutex.user.entity.Group;
import io.mutex.index.valueobject.Constants;
import io.mutex.index.valueobject.ElApiUtil;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.index.valueobject.SuggestionProperty;
import io.mutex.index.valueobject.VirtualPageProperty;


/**
 *
 * @author Florent
 */
@Stateless
public class SuggestService{

    private static final Logger LOG = Logger.getLogger(SuggestService.class.getName());
    
    @Inject ElApiUtil elApiUtil;
    @Inject SearchCoreService coreSearchService;
    @Inject UserGroupServiceImpl userGroupService;
    @Inject EnvironmentUtils envUtils;
    
    public List<MutexTermSuggestion> suggestTerm(List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> suggestTerm_(gps,text)).orElseGet(() -> Collections.EMPTY_LIST);
        }else{
            return suggestTerm_(selectedGroups,text);
        }
    }
    
    public List<MutexPhraseSuggestion> suggestPhrase(List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> suggestPhrase_(gps,text))
                    .orElseGet(() -> Collections.EMPTY_LIST);
        }else{
            return suggestPhrase_(selectedGroups,text);
        }
    }
   
   
    private List<MutexTermSuggestion> suggestTerm_(List<Group> groups,String text){
        Optional<SearchRequest> rSearchRequest = getTermSuggestionBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(tsb -> getTermSuggestBuilder(tsb))
                .flatMap(sb -> coreSearchService.makeSearchSourceBuilder(sb))
                .flatMap(ssb -> coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
//        rSearchRequest.forEach(r -> elApiUtil.logJson(r));
                
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> coreSearchService.search(sr));
        return rResponse.map(r -> toMutexTermSuggestion(r)).orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    private List<MutexPhraseSuggestion> suggestPhrase_(List<Group> groups,String text){
        Optional<SearchRequest> rSearchRequest = getPhraseSuggestionBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(tsb -> getPhraseSuggestBuilder(tsb))
                .flatMap(sb -> coreSearchService.makeSearchSourceBuilder(sb))
                .flatMap(ssb -> coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> coreSearchService.search(sr));
        return rResponse.map(r -> toMutexPhraseSuggestion(r)).orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    private void completeTerm(List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .ifPresent(gps -> complete(gps,text));
        }else{
            complete(selectedGroups,text); 
        }
    }
    
   
//    private List<MutexCompletionSuggestion> processCompleteStack(List<Group> groups,String text){
//        complete(groups, text);
//        LOG.log(Level.INFO, "--> AUTO COMPLETION LIST SIZE: {0}", completionSuggestions.size());
//        completionSuggestions.forEach(c -> LOG.log(Level.INFO, "--> COMPLETION TERM: {0}", c.getContent()));
//    }
//    
    
    public List<MutexCompletionSuggestion> complete(List<Group> groups,String prefix){
        LOG.log(Level.INFO,"---- SUGGEST COMPLETION ----");
//        completionQueryJson();
        
        Optional<SearchRequest> rSearchRequest = 
                 buildCompletionSuggestionBuilder("term_completion", prefix)
                .flatMap(csb -> addSuggestion(new SuggestBuilder(),csb))
                .flatMap(sb -> coreSearchService.makeSearchSourceBuilder(sb))
                .flatMap(ssb -> coreSearchService.getTermCompleteRequest(groups,ssb));
       
//        rSearchRequest.forEachOrThrow(s ->  LOG.log(Level.INFO, "--> REQUEST QUERY: {0}", s));
        rSearchRequest.ifPresentOrElse(r -> elApiUtil.logJson(r),
                () -> LOG.log(Level.SEVERE, "EXCEPTION WHEN SERACHING"));
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> coreSearchService.search(sr));
        rResponse.ifPresentOrElse(r -> elApiUtil.logJson(r),
                () -> LOG.log(Level.SEVERE, "EXCEPTION WHEN SERACHING"));
        
        return rResponse.map(r -> toMutexCompletionSuggestion(r)).orElseGet(() -> Collections.EMPTY_LIST);
        
//        return Collections.EMPTY_LIST;
        
    }
     
    private Optional<CompletionSuggestionBuilder> buildCompletionSuggestionBuilder(String fieldName,String prefix){
        CompletionSuggestionBuilder completionSuggestionBuilder = 
                SuggestBuilders.completionSuggestion(fieldName)
                        .prefix(prefix).skipDuplicates(true);
        return Optional.of(completionSuggestionBuilder);
    }
    
    private Optional<SuggestBuilder> addSuggestion(SuggestBuilder suggestBuilder,
            CompletionSuggestionBuilder completionSuggestionBuilder){
        
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("content_completion",completionSuggestionBuilder);
//        LOG.log(Level.INFO, "--> COMPLETION SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return Optional.of(suggestBuilder);
    }
     
    private List<MutexTermSuggestion> toMutexTermSuggestion(SearchResponse searchResponse){
        Suggest suggest = searchResponse.getSuggest();
        TermSuggestion termSuggestion = suggest.getSuggestion(SuggestionProperty.CONTENT_TERM_SUGGESTION.value()); 
        return termSuggestion.getEntries().stream().map(e -> e.getOptions())
                .flatMap(List::stream).map(MutexTermSuggestion::new)
                .collect(Collectors.toList());
    }
    
    private List<MutexPhraseSuggestion> toMutexPhraseSuggestion(SearchResponse searchResponse){
        Suggest suggest = searchResponse.getSuggest();
        PhraseSuggestion phraseSuggestion = suggest.getSuggestion(SuggestionProperty.CONTENT_PHRASE_SUGGESTION.value()); 
          
        return phraseSuggestion.getEntries().stream().map(e -> e.getOptions())
                .flatMap(List::stream).map(MutexPhraseSuggestion::new)
                .collect(Collectors.toList());
    }
    
    private List<MutexCompletionSuggestion> toMutexCompletionSuggestion(SearchResponse searchResponse){
        Suggest suggest = searchResponse.getSuggest();
        CompletionSuggestion completionSuggestion = suggest
                .getSuggestion("content_completion"); 
          
        return completionSuggestion.getEntries().stream().map(e -> e.getOptions())
                .flatMap(List::stream).map(MutexCompletionSuggestion::new)
                .collect(Collectors.toList());
    }
 
    private Optional<SuggestionBuilder> getTermSuggestionBuilder(String fieldName,String text){
        SuggestionBuilder termSuggestionBuilder =
                SuggestBuilders
                        .termSuggestion(fieldName)
                        .text(text);
        
        return Optional.of(termSuggestionBuilder);
    }
    
    private Optional<SuggestionBuilder> getPhraseSuggestionBuilder(String fieldName,String text){
        SuggestionBuilder phraseSuggestionBuilder =
                SuggestBuilders
                        .phraseSuggestion(fieldName)
                        .highlight(Constants.HIGHLIGHT_PRE_TAG, Constants.HIGHLIGHT_POST_TAG)
                        .text(text);
        return Optional.of(phraseSuggestionBuilder);
    }
    
    private Optional<SuggestBuilder> getTermSuggestBuilder(SuggestionBuilder suggestionBuilder){
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder
                .addSuggestion(SuggestionProperty.CONTENT_TERM_SUGGESTION.value(),
                    suggestionBuilder);
        LOG.log(Level.INFO, "--> TERM SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return Optional.of(suggestBuilder);
    }
    
     private Optional<SuggestBuilder> getPhraseSuggestBuilder(SuggestionBuilder suggestionBuilder){
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder
                .addSuggestion(SuggestionProperty.CONTENT_PHRASE_SUGGESTION.value(),
                    suggestionBuilder);
//        LOG.log(Level.INFO, "--> PHRASE SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return Optional.of(suggestBuilder);
    }
     
}
