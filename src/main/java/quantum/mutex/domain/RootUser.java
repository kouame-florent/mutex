/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 *
 * @author Florent
 */

@Table(name = "mx_root_user")
@Entity
public class RootUser extends User{

    public RootUser() {
    }

    public RootUser(String login, Tenant tenant) {
        super(login, tenant);
    }
    
    
}