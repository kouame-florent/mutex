/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.domain.valueobject;

/**
 *
 * @author Florent
 */
public enum GroupType {
    
    PRIMARY("PRIMARY"),
    SECONDARY("SECONDARY");
    
    public String value;
    
    private GroupType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
