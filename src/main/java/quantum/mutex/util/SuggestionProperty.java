/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.util;

/**
 *
 * @author Florent
 */
public enum SuggestionProperty {
    
    CONTENT_SUGGESTION("content_suggestion"),
    TITLE_SUGGESTION("title_suggestion");
    
    private final String value;
    
    private SuggestionProperty(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
