/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.domain.valueobject;

/**
 *
 * @author florent
 */
public enum UserStatus {
    
    ENABLED("ENABLED"),
    DISABLED("DISABLED"),
    DELETED("DELETED");
    
    private final String value;
    
    private UserStatus(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
