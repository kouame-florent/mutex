/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;



/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "StandardUser.findByLogin",
        query = "SELECT s FROM StandardUser s WHERE s.login = :login"
    ),
   @NamedQuery(
        name = "StandardUser.findByTenant",
        query = "SELECT s FROM StandardUser s WHERE s.tenant = :tenant"
    ),
})
//@Table(name = "mx_standard_user")
@Entity
public class StandardUser extends User{

    public StandardUser() {
    }

    public StandardUser(String login, Tenant tenant) {
        super(login, tenant);
    }
    
    
}
