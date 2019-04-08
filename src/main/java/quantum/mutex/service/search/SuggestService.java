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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion.Entry.Option;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.Suggestion;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.SuggestionProperty;
import quantum.mutex.util.VirtualPageProperty;

/**
 *
 * @author Florent
 */
@Stateless
public class SuggestService extends SearchBaseService{

    private static final Logger LOG = Logger.getLogger(SuggestService.class.getName());
     
    
    public List<Suggestion> suggest(List<Group> groups,String text){
        Result<SearchRequest> rSearchRequest = getTermSuggestionBuilder(VirtualPageProperty.CONTENT.value(), text)
                .flatMap(tsb -> getSuggestBuilder(tsb))
                .flatMap(sb -> getSearchSourceBuilder(sb))
                .flatMap(ssb -> getSearchRequest(groups,ssb));
        
        Result<SearchResponse> rResponse = rSearchRequest.flatMap(sr -> search(sr));
        
        return rResponse.map(r -> toSuggestion(r)).getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    private List<Suggestion> toSuggestion(SearchResponse searchResponse){
        Suggest suggest = searchResponse.getSuggest();
        TermSuggestion termSuggestion = suggest.getSuggestion(SuggestionProperty.CONTENT_SUGGESTION.value()); 
        return termSuggestion.getEntries().stream().map(e -> e.getOptions())
                .flatMap(List::stream).map(Suggestion::new)
                .collect(Collectors.toList());
    }
 
    private Result<SuggestionBuilder> getTermSuggestionBuilder(String fieldName,String text){
        SuggestionBuilder termSuggestionBuilder =
                SuggestBuilders
                        .termSuggestion(fieldName)
                        .text(text);
        
        return Result.of(termSuggestionBuilder);
    }
    
    private Result<SuggestBuilder> getSuggestBuilder(SuggestionBuilder suggestionBuilder){
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder
                .addSuggestion(SuggestionProperty.CONTENT_SUGGESTION.value(),
                    suggestionBuilder);
        LOG.log(Level.INFO, "--> TERM SUGGESTION QUERY: {0}",suggestBuilder.toString());
        return Result.of(suggestBuilder);
    }

}
