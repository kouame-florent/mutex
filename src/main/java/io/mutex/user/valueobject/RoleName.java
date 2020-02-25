/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.valueobject;

/**
 *
 * @author Florent
 */
public enum RoleName {
    ADMINISTRATOR("ADMINISTRATOR"),
//    ROOT("ROOT"),
    USER("USER");
    
    private final String value;
    
    private RoleName(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
