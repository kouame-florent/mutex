/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.suggest.term.TermSuggestion;

/**
 *
 * @author Florent
 */
@Getter @Setter
public class MutexTermSuggestion {
    
    private String content;
    private float score;

    public MutexTermSuggestion() {
    }
    
    public MutexTermSuggestion(TermSuggestion.Entry.Option option) {
        this.content = option.getText().string();
        this.score = option.getScore();
    }
    
    
}
