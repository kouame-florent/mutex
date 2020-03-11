/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;


import io.mutex.index.service.ElApiLogUtil;
import io.mutex.index.service.MutexPageServiceImpl;
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
import io.mutex.search.valueobject.CompletionSuggestionFragment;
import io.mutex.search.valueobject.PhraseSuggestionFragment;
import io.mutex.search.valueobject.TermSuggestionFragment;
import io.mutex.user.entity.Group;
import io.mutex.index.valueobject.Constants;
import io.mutex.index.service.ElApiLogUtilImpl;
import io.mutex.index.service.MutexPageService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.index.valueobject.SuggestionProperty;
import io.mutex.user.service.UserGroupService;


/**
 *
 * @author Florent
 */
@Stateless
public class SuggestService{

    private static final Logger LOG = Logger.getLogger(SuggestService.class.getName());
    
    @Inject ElApiLogUtil elApiUtil;
    @Inject Helper helper;
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils envUtils;
    @Inject MutexPageService virtualPageService;
    
    public List<TermSuggestionFragment> suggest(List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> suggestTerm(gps,text)).orElseGet(() -> Collections.EMPTY_LIST);
        }else{
            return suggestTerm(selectedGroups,text);
        }
    }
    
    public List<PhraseSuggestionFragment> suggestPhrase(List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> doSuggestPhrase(gps,text))
                    .orElseGet(() -> Collections.EMPTY_LIST);
        }else{
            return doSuggestPhrase(selectedGroups,text);
        }
    }
   
   
    private List<TermSuggestionFragment> suggestTerm(List<Group> groups,String text){
        
        Optional<SearchRequest> oSearchRequest =
            Optional.ofNullable(termSuggestionBuilder(virtualPageService.contentMappingProperty(), text))
                .map(tsb -> termSuggestBuilder(tsb))
                .map(sb -> helper.searchSourceBuilder(sb))
                .map(ssb -> helper.searchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
 
        oSearchRequest.ifPresent(r -> elApiUtil.logJson(r));
                
        Optional<SearchResponse> rResponse = oSearchRequest.flatMap(helper::search);
        return rResponse.map(r -> termsSuggestion(r)).orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    private List<PhraseSuggestionFragment> doSuggestPhrase(List<Group> groups,String text){
        Optional<SearchRequest> rSearchRequest = 
            Optional.ofNullable(phraseSuggestionBuilder(virtualPageService.trigramMappingProperty(), text))
               .map(tsb -> phraseSuggestBuilder(tsb))
               .map(sb -> helper.searchSourceBuilder(sb))
               .map(ssb -> helper.searchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> helper.search(sr));
        return rResponse.map(r -> phraseSuggestion(r)).orElseGet(() -> Collections.EMPTY_LIST);
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
    
    public List<CompletionSuggestionFragment> complete(List<Group> groups,String prefix){
        LOG.log(Level.INFO,"---- SUGGEST COMPLETION ----");
//        completionQueryJson();
        
        Optional<SearchRequest> rSearchRequest = 
            Optional.ofNullable(buildCompletionSuggestionBuilder("term_completion", prefix))
                .map(csb -> addSuggestion(new SuggestBuilder(),csb))
                .map(sb -> helper.searchSourceBuilder(sb))
                .map(ssb -> helper.getTermCompleteRequest(groups,ssb));
       
//        rSearchRequest.forEachOrThrow(s ->  LOG.log(Level.INFO, "--> REQUEST QUERY: {0}", s));
        rSearchRequest.ifPresentOrElse(r -> elApiUtil.logJson(r),
                () -> LOG.log(Level.SEVERE, "EXCEPTION WHEN SERACHING"));
        Optional<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> helper.search(sr));
        rResponse.ifPresentOrElse(r -> elApiUtil.logJson(r),
                () -> LOG.log(Level.SEVERE, "EXCEPTION WHEN SERACHING"));
        
        return rResponse.map(r -> completionsSuggestion(r)).orElseGet(() -> Collections.EMPTY_LIST);
        
//        return Collections.EMPTY_LIST;
        
    }
     
    private CompletionSuggestionBuilder buildCompletionSuggestionBuilder(String fieldName,String prefix){
        CompletionSuggestionBuilder completionSuggestionBuilder = 
                SuggestBuilders.completionSuggestion(fieldName)
                        .prefix(prefix).skipDuplicates(true);
        return completionSuggestionBuilder;
    }
    
    private SuggestBuilder addSuggestion(SuggestBuilder suggestBuilder,
            CompletionSuggestionBuilder completionSuggestionBuilder){
        
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("content_completion",completionSuggestionBuilder);
//        LOG.log(Level.INFO, "--> COMPLETION SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return suggestBuilder;
    }
     
    private List<TermSuggestionFragment> termsSuggestion(SearchResponse searchResponse){
        Suggest suggest = searchResponse.getSuggest();
        TermSuggestion termSuggestion = suggest.getSuggestion(SuggestionProperty.CONTENT_TERM_SUGGESTION.value()); 
        return termSuggestion.getEntries().stream().map(e -> e.getOptions())
                .flatMap(List::stream).map(TermSuggestionFragment::new)
                .collect(Collectors.toList());
    }
    
    private List<PhraseSuggestionFragment> phraseSuggestion(SearchResponse searchResponse){
        Suggest suggest = searchResponse.getSuggest();
        PhraseSuggestion phraseSuggestion = suggest.getSuggestion(SuggestionProperty.CONTENT_PHRASE_SUGGESTION.value()); 
          
        return phraseSuggestion.getEntries().stream().map(e -> e.getOptions())
                .flatMap(List::stream).map(PhraseSuggestionFragment::new)
                .collect(Collectors.toList());
    }
    
    private List<CompletionSuggestionFragment> completionsSuggestion(SearchResponse searchResponse){
        Suggest suggest = searchResponse.getSuggest();
        CompletionSuggestion completionSuggestion = suggest
                .getSuggestion("content_completion"); 
          
        return completionSuggestion.getEntries().stream().map(e -> e.getOptions())
                .flatMap(List::stream).map(CompletionSuggestionFragment::new)
                .collect(Collectors.toList());
    }
 
    private SuggestionBuilder termSuggestionBuilder(String fieldName,String text){
        SuggestionBuilder termSuggestionBuilder =
                SuggestBuilders
                        .termSuggestion(fieldName)
                        .text(text)
                        .size(Constants.TERM_SUGGESTION_MAX_RESULT_SIZE);
        
        return termSuggestionBuilder;
    }
    
    private SuggestionBuilder phraseSuggestionBuilder(String fieldName,String text){
        SuggestionBuilder phraseSuggestionBuilder =
                SuggestBuilders
                        .phraseSuggestion(fieldName)
                        .size(1)
                        
                        .highlight(Constants.HIGHLIGHT_PRE_TAG, Constants.HIGHLIGHT_POST_TAG)
                        .text(text);
        return phraseSuggestionBuilder;
    }
    
    private SuggestBuilder termSuggestBuilder(SuggestionBuilder suggestionBuilder){
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder
                .addSuggestion(SuggestionProperty.CONTENT_TERM_SUGGESTION.value(),
                    suggestionBuilder);
        LOG.log(Level.INFO, "--> TERM SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return suggestBuilder;
    }
    
     private SuggestBuilder phraseSuggestBuilder(SuggestionBuilder suggestionBuilder){
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder
                .addSuggestion(SuggestionProperty.CONTENT_PHRASE_SUGGESTION.value(),
                    suggestionBuilder);
//        LOG.log(Level.INFO, "--> PHRASE SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return suggestBuilder;
    }
     
}
