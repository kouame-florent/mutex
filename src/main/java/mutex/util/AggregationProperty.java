/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.util;

/**
 *
 * @author Florent
 */
public enum AggregationProperty {
    
    PAGE_TERMS_VALUE("top_virtual_pages"),
    PAGE_FIELD_VALUE("inode_uuid"),
    PAGE_TOP_HITS_VALUE("top_hits"),
    META_TERMS_VALUE("top_metas"),
    META_FIELD_VALUE("inode_uuid"),
    META_TOP_HITS_VALUE("top_hits");
    
    private final String value;
    
    private AggregationProperty(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
