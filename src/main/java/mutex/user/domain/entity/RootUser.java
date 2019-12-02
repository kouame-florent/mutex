/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.domain.entity;

import mutex.user.domain.entity.Tenant;
import mutex.user.domain.entity.User;
import javax.persistence.Entity;



/**
 *
 * @author Florent
 */

//@Table(name = "mx_root_user")
@Entity
public class RootUser extends User{

    public RootUser() {
    }

    public RootUser(String login, Tenant tenant) {
        super(login, tenant);
    }
    
    
}
