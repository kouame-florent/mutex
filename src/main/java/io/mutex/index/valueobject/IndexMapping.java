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
public enum IndexMapping {
    METADATA("template/metadata_mapping.json"),
    VIRTUAL_PAGE("template/virtual_page_mapping.json"),
    TERM_COMPLETION("template/term_completion_mapping.json"),
    PHRASE_COMPLETION("template/phrase_completion_mapping.json"),
    UTIL("template/util_mapping.json");
    
    private final String value;
    
    private IndexMapping(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
