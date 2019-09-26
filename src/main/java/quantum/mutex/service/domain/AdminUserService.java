/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.domain;


import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.entity.AdminUser;
import quantum.mutex.domain.entity.UserStatus;
import quantum.mutex.domain.dao.AdminUserDAO;


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
