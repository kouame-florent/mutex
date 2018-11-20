/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.entity;

/**
 *
 * @author Florent
 */
public enum Permission {
    
    READ("READ"),
    DELETE("DELETE");
    
    private final String value;
    
    private Permission(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    
}
