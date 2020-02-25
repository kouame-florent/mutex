/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.valueobject;


/**
 *
 * @author florent
 */

public enum ContextIdParamKey {
    
    ADMIN_UUID("ADMIN_UUID"),
    GROUP_UUID("GROUP_UUID"),
    INODE_UUID("INODE_UUID"),
    SPACE_UUID("SPACE_UUID"),
    USER_UUID("USER_UUID");
     

    private final String param;

    private ContextIdParamKey(String value){
            this.param = value;
    }

    public String param() {
            return param;
    }
    
}
