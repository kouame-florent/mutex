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
public enum IndexNameSuffix {
    METADATA("metadata"),
    VIRTUAL_PAGE("virtual_page"),
    TERM_COMPLETION("term_completion"),
    PHRASE_COMPLETION("phrase_completion"),
    MUTEX_UTIL("mutex_util");
    
    private final String suffix;
    
    private IndexNameSuffix(String value){
        this.suffix = value;
    }

    public String suffix() {
        return suffix;
    }
}
