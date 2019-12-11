/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;


/**
 *
 * @author Florent
 */

public class MutexPhraseSuggestion extends MutexSuggestion{
    public MutexPhraseSuggestion(PhraseSuggestion.Entry.Option option) {
        super(option.getText().string(), option.getScore());
    }
 }
