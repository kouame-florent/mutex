/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

/**
 *
 * @author florent
 */
public enum DialogParamKey {
    
    GROUP_ID("GROUP_ID"),
    TENANT_ID("TENANT_ID"),
    USER_ID("USER_ID");
     

    private final String value;

    private DialogParamKey(String value){
            this.value = value;
    }

    public String getValue() {
            return value;
    }
    
}
