/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import java.util.Collections;
import java.util.List;
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
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.MutexCompletionSuggestion;
import quantum.mutex.domain.dto.MutexPhraseSuggestion;
import quantum.mutex.domain.dto.MutexTermSuggestion;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.Constants;
import quantum.mutex.util.ElasticApiUtils;
import quantum.mutex.util.SuggestionProperty;
import quantum.mutex.util.VirtualPageProperty;

/**
 *
 * @author Florent
 */
@Stateless
public class SuggestService extends SearchBaseService{

    private static final Logger LOG = Logger.getLogger(SuggestService.class.getName());
    
    @Inject ElasticApiUtils elasticApiUtils;
   
    public List<MutexTermSuggestion> suggestTerm(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = getTermSuggestionBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(tsb -> getTermSuggestBuilder(tsb))
                .flatMap(sb -> getSearchSourceBuilder(sb))
                .flatMap(ssb -> getSearchRequest(groups,ssb));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> search(sr));
        
        return rResponse.map(r -> toMutexTermSuggestion(r)).getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    public List<MutexPhraseSuggestion> suggestPhrase(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = getPhraseSuggestionBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(tsb -> getPhraseSuggestBuilder(tsb))
                .flatMap(sb -> getSearchSourceBuilder(sb))
                .flatMap(ssb -> getSearchRequest(groups,ssb));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> search(sr));
        return rResponse.map(r -> toMutexPhraseSuggestion(r)).getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    public List<MutexCompletionSuggestion> suggestCompletion(List<Group> groups,String prefix){
        Result<SearchRequest> rSearchRequest = 
                 getCompletionSuggestionBuilder("term_completion", prefix)
                .flatMap(csb -> getCompletionSuggestBuilder(csb))
                .flatMap(sb -> getSearchSourceBuilder(sb))
                .flatMap(ssb -> getCompleteRequest(groups,ssb));
        
        rSearchRequest.forEach(sr -> elasticApiUtils.logJson(sr));
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> search(sr));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                .forEach(e -> LOG.log(Level.INFO, "{0}", e));
        
        return rResponse.map(r -> toMutexCompletionSuggestion(r)).getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    private Result<CompletionSuggestionBuilder> getCompletionSuggestionBuilder(String fieldName,String prefix){
        CompletionSuggestionBuilder completionSuggestionBuilder = 
                SuggestBuilders.completionSuggestion(fieldName)
                        .prefix(prefix).skipDuplicates(true);
        return Result.of(completionSuggestionBuilder);
    }
    
    private Result<SuggestBuilder> getCompletionSuggestBuilder(CompletionSuggestionBuilder completionSuggestionBuilder){
        
        SuggestBuilder suggestBuilder = new SuggestBuilder();
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
        LOG.log(Level.INFO, "--> PHRASE SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return Result.of(suggestBuilder);
    }
     
}
