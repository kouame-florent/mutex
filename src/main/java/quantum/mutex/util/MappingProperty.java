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
public enum MappingProperty {
    CONTENT_SUGGEST("content.suggest"),
    TERM_COMPLETION("term_completion");
   
    private final String value;
    
    private MappingProperty(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
