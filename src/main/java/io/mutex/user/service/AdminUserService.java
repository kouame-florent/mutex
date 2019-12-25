/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;


import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;
import io.mutex.user.valueobject.RoleName;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.repository.AdminUserDAO;
import io.mutex.index.service.FileIOService;
import io.mutex.shared.service.EncryptionService;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import java.util.List;

/**
 *
 * @author Florent
 */
@Stateless
public class AdminUserService {
	
	private static final Logger LOG = Logger.getLogger(AdminUserService.class.getName());
    
    @Inject AdminUserDAO adminUserDAO;
    @Inject UserRoleService userRoleService;
   
    public Optional<AdminUser> createAdminUser(AdminUser adminUser) throws AdminUserExistException,
	    NotMatchingPasswordAndConfirmation{
    	
    	if(!arePasswordsMatch(adminUser)){
    		throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
    	}
    	
  		if(isAdminWithLoginExist(adminUser.getLogin())){
  			throw new AdminUserExistException("Ce login existe déjà");
		}
  		
  		adminUser.setPassword(EncryptionService.hash(adminUser.getPassword()));
		adminUser.setStatus(UserStatus.DISABLED);
	    return adminUserDAO.makePersistent(adminUser);
	
	}
   
    private boolean arePasswordsMatch(User user) throws NotMatchingPasswordAndConfirmation{
        return user.getPassword().equals(user.getConfirmPassword());
       
    }
    
    
    private boolean isAdminWithLoginExist(String login){
        Optional<AdminUser> oTenant = adminUserDAO.findByLogin(login);
        return oTenant.isPresent();
    }
    
       
    public Optional<AdminUser> updateAdminUser(AdminUser adminUser) throws AdminLoginExistException,
    			NotMatchingPasswordAndConfirmation{
    	
    	if(!arePasswordsMatch(adminUser)){
    		throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
    	}
        
        Optional<AdminUser> oAdminByName = adminUserDAO.findByLogin(adminUser.getLogin());
        String mnguuid = oAdminByName.map(AdminUser::getUuid).orElse("");
        LOG.log(Level.INFO, "[mutex] managed uuid:{0}", mnguuid);
        LOG.log(Level.INFO, "[mutex] transient uuid:{0}",adminUser.getUuid());
        
        
        if((oAdminByName.isPresent() && oAdminByName.filter(a -> a.equals(adminUser)).isEmpty()) ){
        	throw new AdminLoginExistException("Ce login existe déjà");
        	           
        }
       
        return adminUserDAO.makePersistent(adminUser);
    }
        
    
    public Optional<UserRole> createAdminUserRole(AdminUser adminUser){
        return userRoleService.create(adminUser, RoleName.ADMINISTRATOR);
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
