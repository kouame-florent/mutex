/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;


/**
 *
 * @author Florent
 */

public class MutexCompletionSuggestion {
	
	@Getter @Setter
    private String content;
	@Getter @Setter
    private float score;

    public MutexCompletionSuggestion() {
    }
    
    public MutexCompletionSuggestion(CompletionSuggestion.Entry.Option option) {
        this.content = option.getText().string();
        this.score = option.getScore();
    }
    
    
}
