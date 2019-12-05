/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.domain.valueobject;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.term.TermSuggestion;

/**
 *
 * @author Florent
 */

public class MutexTermSuggestion extends MutexSuggestion{
    public MutexTermSuggestion(TermSuggestion.Entry.Option option) {
        super(option.getText().string(), option.getScore());
    }
    
}
