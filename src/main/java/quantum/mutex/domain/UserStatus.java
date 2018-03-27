/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

/**
 *
 * @author florent
 */
public enum UserStatus {
    
    ENABLED("ENABLED"),
    DESABLED("DISABLED"),
    DELETED("DELETED");
    
    public String value;
    
    private UserStatus(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
