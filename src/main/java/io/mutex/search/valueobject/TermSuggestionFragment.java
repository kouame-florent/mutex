/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;


import org.elasticsearch.search.suggest.term.TermSuggestion;

/**
 *
 * @author Florent
 */

public class TermSuggestionFragment extends SuggestionFragment{
    public TermSuggestionFragment(TermSuggestion.Entry.Option option) {
        super(option.getText().string(), option.getScore());
    }
    
}
