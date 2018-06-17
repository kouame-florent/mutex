/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

/**
 *
 * @author Florent
 */
public enum RoleValue {
    ADMINISTRATOR("ADMINISTRATOR"),
    ROOT("ROOT"),
    USER("USER");
    
    private final String value;
    
    private RoleValue(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
