/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;


/**
 *
 * @author florent
 */

public enum ViewParamKey {
    
    ADMIN_UUID("ADMIN_UUID"),
    GROUP_UUID("GROUP_UUID"),
    TENANT_UUID("TENANT_UUID"),
    USER_UUID("USER_UUID");
     

    private final String param;

    private ViewParamKey(String value){
            this.param = value;
    }

    public String param() {
            return param;
    }
    
}
