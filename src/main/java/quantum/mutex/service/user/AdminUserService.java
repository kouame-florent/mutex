/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.user;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Result;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.UserStatus;
import quantum.mutex.domain.dao.AdminUserDAO;

/**
 *
 * @author Florent
 */
@Stateless
public class AdminUserService {
    
    @Inject AdminUserDAO adminUserDAO;
    
//    public Optional<AdminUser> updateStatus(@NotNull AdminUser adminUser){
//        adminUser.setStatus(UserStatus.DISABLED);
//        return adminUserDAO.makePersistent(adminUser);
//    }
//    
    public Result<AdminUser> resetTenant(@NotNull AdminUser adminUser){
        adminUser.setTenant(null);
        adminUser.setStatus(UserStatus.DISABLED);
        return adminUserDAO.makePersistent(adminUser);
    }
    
}
