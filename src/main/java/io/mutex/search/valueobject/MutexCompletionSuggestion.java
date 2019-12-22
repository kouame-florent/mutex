/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import org.elasticsearch.search.suggest.completion.CompletionSuggestion;


/**
 *
 * @author Florent
 */

public class MutexCompletionSuggestion extends MutexSuggestion{
    public MutexCompletionSuggestion(CompletionSuggestion.Entry.Option option) {
        super(option.getText().string(), option.getScore());
    }
    
}
