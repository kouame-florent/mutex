/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.valueobject;

/**
 *
 * @author Florent
 */
public enum SuggestionProperty {
    
    CONTENT_TERM_SUGGESTION("content_term_suggestion"),
    CONTENT_COMPLETION_SUGGESTION("content_completion_suggestion"),
    CONTENT_PHRASE_SUGGESTION("content_phrase_suggestion"),
    TITLE_TERM_SUGGESTION("title_term_suggestion"),
    TITLE_COMPLETION_SUGGESTION("title_completion_suggestion"),
    TITLE_PHRASE_SUGGESTION("title_phrase_suggestion");
    
    private final String value;
    
    private SuggestionProperty(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
