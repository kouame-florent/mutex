/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service.impl;


import java.util.Optional;
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
import io.mutex.shared.service.EncryptionService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.service.AdminUserService;
import java.util.List;

/**
 *
 * @author Florent
 */
@Stateless
public class AdminUserServiceImpl implements AdminUserService {
	
    private static final Logger LOG = Logger.getLogger(AdminUserServiceImpl.class.getName());
    
    @Inject AdminUserDAO adminUserDAO;
    @Inject UserRoleServiceImpl userRoleService;
    @Inject EnvironmentUtils envUtils;
    @Inject StringUtil stringUtil;
   
    @Override
    public Optional<AdminUser> createAdminUser(AdminUser adminUser) throws AdminUserExistException,
	    NotMatchingPasswordAndConfirmation{
    	
    	if(!arePasswordsMatch(adminUser)){
    		throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
    	}
    	
        if(isAdminWithLoginExist(adminUser.getLogin())){
                throw new AdminUserExistException("Ce login existe déjà");
        }
        
        return setEncryptedPassword(adminUser)
                    .map(this::setDisabled)
                    .map(this::loginToLowerCase)
                    .flatMap(adminUserDAO::makePersistent);
  
    }
    
    @Override
    public Optional<AdminUser> updateAdminUser(AdminUser adminUser) throws AdminLoginExistException,
    			NotMatchingPasswordAndConfirmation{
    	
    	if(!arePasswordsMatch(adminUser)){
    		throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
    	}
        
        Optional<AdminUser> oAdminByName = adminUserDAO
                .findByLogin(StringUtil.lowerCaseWithoutAccent(adminUser.getLogin()));
          
        if((oAdminByName.isPresent() && oAdminByName.filter(a -> a.equals(adminUser)).isEmpty()) ){
        	throw new AdminLoginExistException("Ce login existe déjà");
        }
       
        return adminUserDAO.makePersistent(loginToLowerCase(adminUser));
    }
   
    private boolean arePasswordsMatch(User user) throws NotMatchingPasswordAndConfirmation{
        return user.getPassword().equals(user.getConfirmPassword());
       
    }
    
    private AdminUser loginToLowerCase(AdminUser user){
        user.setLogin(StringUtil.lowerCaseWithoutAccent(user.getLogin()));
        return user;
    }
    
    private Optional<AdminUser> setEncryptedPassword(AdminUser adminUser){
        return Optional.ofNullable(adminUser)
                    .map(a -> {
                                a.setPassword(EncryptionService.hash(a.getPassword()));
                                return a; 
                            }
                    );
    }
    
    private AdminUser setDisabled(AdminUser adminUser){
        adminUser.setStatus(UserStatus.DISABLED);
        return adminUser;
    }
    
    private boolean isAdminWithLoginExist(String login){
        Optional<AdminUser> oTenant = adminUserDAO.findByLogin(login);
        return oTenant.isPresent();
    }
  
    @Override
    public Optional<UserRole> createAdminUserRole(AdminUser adminUser){
        return userRoleService.create(adminUser, RoleName.ADMINISTRATOR);
    }
    
    @Override
    public Optional<AdminUser> linkAdminUser(AdminUser adminUser,Tenant tenant){
    	  adminUser.setTenant(tenant);
    	  return adminUserDAO.makePersistent(adminUser);
    }
       
    @Override
    public Optional<AdminUser> unlinkAdminUser(AdminUser adminUser){
        adminUser.setTenant(null);
        return adminUserDAO.makePersistent(adminUser);
    }
    
    @Override
    public Optional<AdminUser> changeAdminUserStatus(AdminUser adminUser,UserStatus status){
        adminUser.setStatus(status);
        return adminUserDAO.makePersistent(adminUser);
    }
    
    @Override
    public List<AdminUser> findNotAssignedToTenant(){
    	return adminUserDAO.findNotAssignedToTenant();
    }
    
    @Override
    public List<AdminUser> findAllAdminUsers(){
        return adminUserDAO.findAll();
    }
    
    @Override
    public Optional<AdminUser> findByTenant(Tenant tenant){
       return adminUserDAO.findByTenant(tenant);
    }
    
    @Override
    public Optional<AdminUser> findByLogin(String login){
       return adminUserDAO.findByLogin(login);
    }
    
    @Override
    public Optional<AdminUser> findByUuid(String uuid){
        return adminUserDAO.findById(uuid);
    }
    
    @Override
    public void delete(AdminUser adminUser){
        Optional.ofNullable(adminUser).ifPresent(adminUserDAO::makeTransient);

    }
    
}
