/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service;


import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.domain.entity.AdminUser;
import io.mutex.domain.valueobject.UserStatus;
import io.mutex.repository.AdminUserDAO;


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
