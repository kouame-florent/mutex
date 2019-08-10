/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import quantum.functional.api.Result;
import quantum.mutex.domain.type.Fragment;
import quantum.mutex.domain.type.MutexCompletionSuggestion;
import quantum.mutex.domain.type.MutexPhraseSuggestion;
import quantum.mutex.domain.type.MutexSuggestion;
import quantum.mutex.domain.type.MutexTermSuggestion;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.util.Constants;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.EnvironmentUtils;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.SuggestionProperty;
import quantum.mutex.util.VirtualPageProperty;

/**
 *
 * @author Florent
 */
@Stateless
public class SuggestService{

    private static final Logger LOG = Logger.getLogger(SuggestService.class.getName());
    
    @Inject ElApiUtil elApiUtil;
    @Inject SearchCoreService coreSearchService;
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils envUtils;
    
    public List<MutexTermSuggestion> suggestTerm(List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> suggestTerm_(gps,text)).getOrElse(() -> Collections.EMPTY_LIST);
        }else{
            return suggestTerm_(selectedGroups,text);
        }
    }
    
    public List<MutexPhraseSuggestion> suggestPhrase(List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            return envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .map(gps -> suggestPhrase_(gps,text))
                    .getOrElse(() -> Collections.EMPTY_LIST);
        }else{
            return suggestPhrase_(selectedGroups,text);
        }
    }
   
   
    private List<MutexTermSuggestion> suggestTerm_(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = getTermSuggestionBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(tsb -> getTermSuggestBuilder(tsb))
                .flatMap(sb -> coreSearchService.makeSearchSourceBuilder(sb))
                .flatMap(ssb -> coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
//        rSearchRequest.forEach(r -> elApiUtil.logJson(r));
                
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> coreSearchService.search(sr));
        return rResponse.map(r -> toMutexTermSuggestion(r)).getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    private List<MutexPhraseSuggestion> suggestPhrase_(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = getPhraseSuggestionBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(tsb -> getPhraseSuggestBuilder(tsb))
                .flatMap(sb -> coreSearchService.makeSearchSourceBuilder(sb))
                .flatMap(ssb -> coreSearchService.getSearchRequest(groups,ssb,IndexNameSuffix.VIRTUAL_PAGE));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> coreSearchService.search(sr));
        return rResponse.map(r -> toMutexPhraseSuggestion(r)).getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    private void completeTerm(List<Group> selectedGroups,String text){
        if(selectedGroups.isEmpty()){
            envUtils.getUser().map(u -> userGroupService.getAllGroups(u))
                    .forEach(gps -> complete(gps,text));
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
        
        Result<SearchRequest> rSearchRequest = 
                 buildCompletionSuggestionBuilder("term_completion", prefix)
                .flatMap(csb -> addSuggestion(new SuggestBuilder(),csb))
                .flatMap(sb -> coreSearchService.makeSearchSourceBuilder(sb))
                .flatMap(ssb -> coreSearchService.getTermCompleteRequest(groups,ssb));
       
//        rSearchRequest.forEachOrThrow(s ->  LOG.log(Level.INFO, "--> REQUEST QUERY: {0}", s));
        rSearchRequest.forEachOrException(r -> elApiUtil.logJson(r))
                .forEach(e -> e.printStackTrace());
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> coreSearchService.search(sr));
        rResponse.forEachOrException(r -> elApiUtil.logJson(r))
                .forEach(e -> LOG.log(Level.INFO, "{0}", e));
        
        return rResponse.map(r -> toMutexCompletionSuggestion(r)).getOrElse(() -> Collections.EMPTY_LIST);
        
//        return Collections.EMPTY_LIST;
        
    }
     
    private Result<CompletionSuggestionBuilder> buildCompletionSuggestionBuilder(String fieldName,String prefix){
        CompletionSuggestionBuilder completionSuggestionBuilder = 
                SuggestBuilders.completionSuggestion(fieldName)
                        .prefix(prefix).skipDuplicates(true);
        return Result.of(completionSuggestionBuilder);
    }
    
    private Result<SuggestBuilder> addSuggestion(SuggestBuilder suggestBuilder,
            CompletionSuggestionBuilder completionSuggestionBuilder){
        
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("content_completion",completionSuggestionBuilder);
//        LOG.log(Level.INFO, "--> COMPLETION SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return Result.of(suggestBuilder);
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
 
    private Result<SuggestionBuilder> getTermSuggestionBuilder(String fieldName,String text){
        SuggestionBuilder termSuggestionBuilder =
                SuggestBuilders
                        .termSuggestion(fieldName)
                        .text(text);
        
        return Result.of(termSuggestionBuilder);
    }
    
    private Result<SuggestionBuilder> getPhraseSuggestionBuilder(String fieldName,String text){
        SuggestionBuilder phraseSuggestionBuilder =
                SuggestBuilders
                        .phraseSuggestion(fieldName)
                        .highlight(Constants.HIGHLIGHT_PRE_TAG, Constants.HIGHLIGHT_POST_TAG)
                        .text(text);
        return Result.of(phraseSuggestionBuilder);
    }
    
    private Result<SuggestBuilder> getTermSuggestBuilder(SuggestionBuilder suggestionBuilder){
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder
                .addSuggestion(SuggestionProperty.CONTENT_TERM_SUGGESTION.value(),
                    suggestionBuilder);
        LOG.log(Level.INFO, "--> TERM SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return Result.of(suggestBuilder);
    }
    
     private Result<SuggestBuilder> getPhraseSuggestBuilder(SuggestionBuilder suggestionBuilder){
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder
                .addSuggestion(SuggestionProperty.CONTENT_PHRASE_SUGGESTION.value(),
                    suggestionBuilder);
//        LOG.log(Level.INFO, "--> PHRASE SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return Result.of(suggestBuilder);
    }
     
}
