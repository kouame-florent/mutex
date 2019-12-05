/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.domain;

import io.mutex.domain.Tenant;
import io.mutex.domain.User;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "AdminUser.findByLogin",
        query = "SELECT a FROM AdminUser a WHERE a.login = :login"
    ),
   @NamedQuery(
        name = "AdminUser.findByTenant",
        query = "SELECT a FROM AdminUser a WHERE a.tenant = :tenant"
    ),
   @NamedQuery(
        name = "AdminUser.findNotAssignedToTenant",
        query = "SELECT a FROM AdminUser a WHERE a.tenant IS NULL"
    ),
})
//@Table(name = "mx_admin_user")
@Entity
public class AdminUser extends User{
    
    
    public AdminUser() {
    }

    public AdminUser(String login, Tenant tenant) {
        super(login, tenant);
    }
    
    
    
}
