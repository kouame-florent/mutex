/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;


/**
 *
 * @author Florent
 */
@Getter @Setter
public class MutexPhraseSuggestion {
    
    private String content;
    private float score;

    public MutexPhraseSuggestion() {
    }
    
    public MutexPhraseSuggestion(PhraseSuggestion.Entry.Option option) {
        this.content = option.getText().string();
        this.score = option.getScore();
    }
    
    
}
