/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;


/**
 *
 * @author Florent
 */

public class PhraseSuggestionFragment extends SuggestionFragment{
    public PhraseSuggestionFragment(PhraseSuggestion.Entry.Option option) {
        super(option.getHighlighted().string(), option.getScore());
    }
 }
