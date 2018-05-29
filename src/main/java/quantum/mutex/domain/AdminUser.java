/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;



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
})
@Entity
public class AdminUser extends User{

    public AdminUser() {
    }

    public AdminUser(String login, Tenant tenant) {
        super(login, tenant);
    }
    
    
    
}
