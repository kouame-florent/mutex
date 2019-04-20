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
public enum CompletionMappingProperty {
    
    TERM_COMPLETION("term_completion");
   
    private final String value;
    
    private CompletionMappingProperty(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
