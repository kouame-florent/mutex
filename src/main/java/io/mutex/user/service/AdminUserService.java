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
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import java.util.List;

/**
 *
 * @author Florent
 */
@Stateless
public class AdminUserService {
    
    @Inject AdminUserDAO adminUserDAO;
    @Inject UserRoleService userRoleService;
    
//    public Optional<AdminUser> createAdminUserAndRole(AdminUser adminUser) throws AdminUserExistException{
//        if(isPasswordValid(adminUser)){
//            Optional<AdminUser> oAdminUsr =  createAdminUser(adminUser);
//            oAdminUsr.ifPresent(this::createAdminUserRole);
//            return oAdminUsr;
//        }else{
//            return Optional.empty();
//        }
//    }
    
   
    private boolean isPasswordValid(User user) throws NotMatchingPasswordAndConfirmation{
        if(user.getPassword().equals(user.getConfirmPassword())){
            return true;
        }else{
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
                
    }
    
    public Optional<AdminUser> createAdminUser(AdminUser adminUser) throws AdminUserExistException,
            NotMatchingPasswordAndConfirmation{
        adminUser.setPassword(EncryptionService.hash(adminUser.getPassword()));
        adminUser.setStatus(UserStatus.DISABLED);
        if(isPasswordValid(adminUser) && !isAdminWithLoginExist(adminUser.getLogin())){
            return adminUserDAO.makePersistent(adminUser);
        }
        throw new AdminUserExistException("Ce nom de tenant existe déjà");
        
    }
    
    public Optional<AdminUser> updateAdminUser(AdminUser adminUser){
        
        Optional<AdminUser> oAdminByName = adminUserDAO.findByLogin(adminUser.getLogin());
       
        if((oAdminByName.isPresent() && oAdminByName.filter(t1 -> t1.equals(adminUser)).isPresent()) ){
            return adminUserDAO.makePersistent(adminUser);
        }
          
        if(oAdminByName.isEmpty()){
            return adminUserDAO.makePersistent(adminUser);
        }
        throw new TenantNameExistException("Ce nom de tenant existe déjà");
        
      
    }
        
    
    public Optional<UserRole> createAdminUserRole(AdminUser adminUser){
        return userRoleService.create(adminUser, RoleName.ADMINISTRATOR);
    }
    
    private boolean isAdminWithLoginExist(String login){
        Optional<AdminUser> oTenant = adminUserDAO.findByLogin(login);
        return oTenant.isPresent();
    }
        
    
    public Optional<AdminUser> unlinkAdminUser(AdminUser adminUser){
        adminUser.setTenant(null);
        return adminUserDAO.makePersistent(adminUser);
    }
    
    public Optional<AdminUser> changeAdminUserStatus(AdminUser adminUser,UserStatus status){
        adminUser.setStatus(status);
        return adminUserDAO.makePersistent(adminUser);
    }
    
    public List<AdminUser> findAllAdminUsers(){
        return adminUserDAO.findAll();
    }
    
    public Optional<AdminUser> findByTenant(Tenant tenant){
       return adminUserDAO.findByTenant(tenant);
    }
    
    public Optional<AdminUser> findByLogin(String login){
       return adminUserDAO.findByLogin(login);
    }
    
    public Optional<AdminUser> findByUuid(String uuid){
        return adminUserDAO.findById(uuid);
    }
    
    public void deleteTenant(AdminUser adminUser){
        adminUserDAO.makeTransient(adminUser);
    }
    
}
