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
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;
import io.mutex.user.valueobject.RoleName;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.shared.service.EncryptionService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminExistException;
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
    
    @Inject AdminDAO adminDAO;
    @Inject UserRoleService userRoleService;
    @Inject EnvironmentUtils envUtils;
    @Inject StringUtil stringUtil;
   
    @Override
    public Optional<Admin> createAdmin(Admin admin) throws AdminExistException,
	    NotMatchingPasswordAndConfirmation{
    	
    	if(!arePasswordsMatch(admin)){
    		throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
    	}
    	
        if(isAdminWithLoginExist(admin.getLogin())){
                throw new AdminExistException("Ce login existe déjà");
        }
        
        return setEncryptedPassword(admin)
                    .map(this::setDisabled)
                    .map(this::loginToLowerCase)
                    .flatMap(adminDAO::makePersistent);
  
    }
    
    @Override
    public Optional<Admin> updateAdmin(Admin admin) throws AdminLoginExistException,
    			NotMatchingPasswordAndConfirmation{
    	
    	if(!arePasswordsMatch(admin)){
    		throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
    	}
        
        Optional<Admin> oAdminByName = adminDAO
                .findByLogin(StringUtil.lowerCaseWithoutAccent(admin.getLogin()));
          
        if((oAdminByName.isPresent() && oAdminByName.filter(a -> a.equals(admin)).isEmpty()) ){
        	throw new AdminLoginExistException("Ce login existe déjà");
        }
       
        return adminDAO.makePersistent(loginToLowerCase(admin));
    }
   
    private boolean arePasswordsMatch(User user) throws NotMatchingPasswordAndConfirmation{
        return user.getPassword().equals(user.getConfirmPassword());
       
    }
    
    private Admin loginToLowerCase(Admin user){
        user.setLogin(StringUtil.lowerCaseWithoutAccent(user.getLogin()));
        return user;
    }
    
    private Optional<Admin> setEncryptedPassword(Admin admin){
        return Optional.ofNullable(admin)
                    .map(a -> {
                                a.setPassword(EncryptionService.hash(a.getPassword()));
                                return a; 
                            }
                    );
    }
    
    private Admin setDisabled(Admin admin){
        admin.setStatus(UserStatus.DISABLED);
        return admin;
    }
    
    private boolean isAdminWithLoginExist(String login){
        Optional<Admin> oAdmin = adminDAO.findByLogin(login);
        return oAdmin.isPresent();
    }
  
    @Override
    public Optional<UserRole> createAdminRole(Admin admin){
        return userRoleService.create(admin, RoleName.ADMINISTRATOR);
    }
    
//    @Override
//    public Optional<Admin> linkAdmin(Admin admin,Space space){
//    	  admin.setSpace(space);
//    	  return adminDAO.makePersistent(admin);
//    }
//       
//    @Override
//    public Optional<Admin> unlinkAdmin(Admin admin){
//        admin.setSpace(null);
//        return adminDAO.makePersistent(admin);
//    }
    
    @Override
    public Optional<Admin> changeAdminStatus(Admin admin,UserStatus status){
        admin.setStatus(status);
        return adminDAO.makePersistent(admin);
    }
    
//    @Override
//    public List<Admin> findNotAssignedToSpace(){
//    	return adminDAO.findNotAssignedToSpace();
//    }
    
    @Override
    public List<Admin> findAllAdmins(){
        return adminDAO.findAll();
    }
    
//    @Override
//    public Optional<Admin> findBySpace(Space space){
//       return adminDAO.findBySpace(space);
//    }
    
    @Override
    public Optional<Admin> findByLogin(String login){
       return adminDAO.findByLogin(login);
    }
    
    @Override
    public Optional<Admin> findByUuid(String uuid){
        return adminDAO.findById(uuid);
    }
    
    @Override
    public void deleteAdmin(Admin admin){
        Optional.ofNullable(admin).ifPresent(adminDAO::makeTransient);

    }
    
}
