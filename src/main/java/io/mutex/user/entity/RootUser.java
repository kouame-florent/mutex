/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.user.entity.Tenant;
import io.mutex.user.entity.User;
import javax.persistence.Entity;



/**
 *
 * @author Florent
 */

@Entity
public class RootUser extends User{
   
	private static final long serialVersionUID = 1L;

	public RootUser() {
    }

    public RootUser(String login, Tenant tenant) {
        super(login, tenant);
    }
    
    
}
