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
public enum AggregationProperty {
    
    TERMS_VALUE("top_virtual_pages"),
    FIELD_VALUE("inode_uuid"),
    TOP_HITS_VALUE("top_hits");
    
    private final String value;
    
    private AggregationProperty(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
