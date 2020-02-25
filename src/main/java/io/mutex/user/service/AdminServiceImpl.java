/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;


import java.util.Optional;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;
import io.mutex.user.valueobject.RoleName;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.shared.service.EncryptionService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import java.util.List;
import io.mutex.user.repository.AdminDAO;

/**
 *
 * @author Florent
 */
@Stateless
public class AdminServiceImpl implements AdminService {
	
    private static final Logger LOG = Logger.getLogger(AdminServiceImpl.class.getName());
    
    @Inject AdminDAO adminUserDAO;
    @Inject UserRoleService userRoleService;
    @Inject EnvironmentUtils envUtils;
    @Inject StringUtil stringUtil;
   
    @Override
    public Optional<Admin> createAdminUser(Admin adminUser) throws AdminUserExistException,
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
    public Optional<Admin> updateAdminUser(Admin adminUser) throws AdminLoginExistException,
    			NotMatchingPasswordAndConfirmation{
    	
    	if(!arePasswordsMatch(adminUser)){
    		throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
    	}
        
        Optional<Admin> oAdminByName = adminUserDAO
                .findByLogin(StringUtil.lowerCaseWithoutAccent(adminUser.getLogin()));
          
        if((oAdminByName.isPresent() && oAdminByName.filter(a -> a.equals(adminUser)).isEmpty()) ){
        	throw new AdminLoginExistException("Ce login existe déjà");
        }
       
        return adminUserDAO.makePersistent(loginToLowerCase(adminUser));
    }
   
    private boolean arePasswordsMatch(User user) throws NotMatchingPasswordAndConfirmation{
        return user.getPassword().equals(user.getConfirmPassword());
       
    }
    
    private Admin loginToLowerCase(Admin user){
        user.setLogin(StringUtil.lowerCaseWithoutAccent(user.getLogin()));
        return user;
    }
    
    private Optional<Admin> setEncryptedPassword(Admin adminUser){
        return Optional.ofNullable(adminUser)
                    .map(a -> {
                                a.setPassword(EncryptionService.hash(a.getPassword()));
                                return a; 
                            }
                    );
    }
    
    private Admin setDisabled(Admin adminUser){
        adminUser.setStatus(UserStatus.DISABLED);
        return adminUser;
    }
    
    private boolean isAdminWithLoginExist(String login){
        Optional<Admin> oTenant = adminUserDAO.findByLogin(login);
        return oTenant.isPresent();
    }
  
    @Override
    public Optional<UserRole> createAdminUserRole(Admin adminUser){
        return userRoleService.create(adminUser, RoleName.ADMINISTRATOR);
    }
    
//    @Override
//    public Optional<Admin> linkAdminUser(Admin adminUser,Space tenant){
//    	  adminUser.setTenant(tenant);
//    	  return adminUserDAO.makePersistent(adminUser);
//    }
//       
//    @Override
//    public Optional<Admin> unlinkAdminUser(Admin adminUser){
//        adminUser.setTenant(null);
//        return adminUserDAO.makePersistent(adminUser);
//    }
    
    @Override
    public Optional<Admin> changeAdminUserStatus(Admin adminUser,UserStatus status){
        adminUser.setStatus(status);
        return adminUserDAO.makePersistent(adminUser);
    }
    
//    @Override
//    public List<Admin> findNotAssignedToTenant(){
//    	return adminUserDAO.findNotAssignedToTenant();
//    }
    
    @Override
    public List<Admin> findAllAdminUsers(){
        return adminUserDAO.findAll();
    }
    
    @Override
    public Optional<Admin> findBySpace(Space space){
       return adminUserDAO.findBySpace(space);
    }
    
    @Override
    public Optional<Admin> findByLogin(String login){
       return adminUserDAO.findByLogin(login);
    }
    
    @Override
    public Optional<Admin> findByUuid(String uuid){
        return adminUserDAO.findById(uuid);
    }
    
    @Override
    public void delete(Admin adminUser){
        Optional.ofNullable(adminUser).ifPresent(adminUserDAO::makeTransient);

    }
    
}
