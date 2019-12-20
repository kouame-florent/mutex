/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;


import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;
import io.mutex.user.valueobject.RoleName;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.repository.AdminUserDAO;
import io.mutex.shared.service.EncryptionService;
import java.util.List;

/**
 *
 * @author Florent
 */
@Stateless
public class AdminUserService {
    
    @Inject AdminUserDAO adminUserDAO;
    @Inject UserRoleService userRoleService;
    
    public Optional<AdminUser> createAdminUserAndRole(AdminUser adminUser){
        if(isPasswordValid(adminUser)){
            Optional<AdminUser> oAdminUsr =  createAdminUser(adminUser);
            oAdminUsr.ifPresent(this::createUserRole);
            return oAdminUsr;
        }else{
            return Optional.empty();
        }
    }
    
    private boolean isPasswordValid(User user){
        return user.getPassword().equals(user.getConfirmPassword());
    }
    
    public Optional<AdminUser> createAdminUser(AdminUser adminUser){
        adminUser.setPassword(EncryptionService.hash(adminUser.getPassword()));
        adminUser.setStatus(UserStatus.DISABLED);
        return adminUserDAO.makePersistent(adminUser);
    }
    
    private Optional<UserRole> createUserRole(AdminUser adminUser){
        return userRoleService.create(adminUser, RoleName.ADMINISTRATOR);
    }
    
    public Optional<AdminUser> updateAdminUser(AdminUser adminUser){
       return adminUserDAO.makePersistent(adminUser);
    }
        
    public Optional<AdminUser> changeAdminUserStatus(AdminUser adminUser){
        adminUser.setTenant(null);
        adminUser.setStatus(UserStatus.DISABLED);
        return adminUserDAO.makePersistent(adminUser);
    }
    
    public List<AdminUser> findAllAdminUsers(){
        return adminUserDAO.findAll();
    }
    
    public List<AdminUser> findByTenant(Tenant tenant){
       return adminUserDAO.findByTenant(tenant);
    }
    
    public Optional<AdminUser> findByUuid(String uuid){
        return adminUserDAO.findById(uuid);
    }
    
    public void deleteTenant(AdminUser adminUser){
        adminUserDAO.makeTransient(adminUser);
    }
    
}