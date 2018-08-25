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
public enum TenantStatus {
    
    ENABLED("ENABLED"),
    DISABLED("DISABLED"),
    DELETED("DELETED");
    
    private final String value;
    
    private TenantStatus(String value){
        this.value = value;
    }
    
    public String value(){
        return value;
    }
}
