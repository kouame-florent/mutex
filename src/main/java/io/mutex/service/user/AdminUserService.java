/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service.user;


import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.domain.entity.AdminUser;
import io.mutex.domain.entity.Tenant;
import io.mutex.domain.entity.User;
import io.mutex.domain.entity.UserRole;
import io.mutex.domain.valueobject.RoleName;
import io.mutex.domain.valueobject.UserStatus;
import io.mutex.repository.AdminUserDAO;
import io.mutex.service.EncryptionService;
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
