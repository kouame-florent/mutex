/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.service;


import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.user.domain.entity.AdminUser;
import quantum.mutex.user.domain.valueobject.UserStatus;
import quantum.mutex.user.repository.AdminUserDAO;


/**
 *
 * @author Florent
 */
@Stateless
public class AdminUserService {
    
    @Inject AdminUserDAO adminUserDAO;
        
    public Optional<AdminUser> resetTenant( AdminUser adminUser){
        adminUser.setTenant(null);
        adminUser.setStatus(UserStatus.DISABLED);
        return adminUserDAO.makePersistent(adminUser);
    }
    
}
