/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.shared.service;


import io.mutex.index.service.FileIOService;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import io.mutex.user.entity.Role;
import io.mutex.user.valueobject.RoleName;
//import io.mutex.user.entity.RootUser;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.RoleDAO;
import io.mutex.user.repository.UserDAO;
import io.mutex.user.repository.UserGroupDAO;
import io.mutex.user.repository.UserRoleDAO;
import io.mutex.user.entity.Space;
import io.mutex.index.valueobject.Constants;
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Group;
import io.mutex.user.repository.AdminDAO;
import io.mutex.user.repository.SpaceDAO;


/**
 *
 * @author Florent
 */
@Singleton
@Startup
public class ApplicationBootstrap {

    private static final Logger LOG = Logger.getLogger(ApplicationBootstrap.class.getName());
    
    
    @Inject FileIOService fileService;
    @Inject SpaceDAO spaceDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserDAO userDAO;
    @Inject AdminDAO adminDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserRoleDAO userRoleDAO;
    
    @PostConstruct
    public void init(){
        fileService.createHomeDir();
        fileService.createStoreDir();
        fileService.createIndexDir();
      
        createDefaultRoles();
        initAdminDefaultProperties();
      
    }
    
    
    public void createDefaultRoles(){
//        Optional<Role> rRole = roleDAO.findByName(RoleName.ROOT);
        Optional<Role> uRole = roleDAO.findByName(RoleName.USER);
        Optional<Role> aRole = roleDAO.findByName(RoleName.ADMINISTRATOR);
        
//        rRole.ifPresentOrElse(
//            r -> {LOG.log(Level.INFO, "ROOT ROLE NAME: {0}", r.getName());}, 
//            () -> {
//                Role rootRole = new Role(RoleName.ROOT);
//                roleDAO.makePersistent(rootRole);
//            }
//        );
        
        uRole.ifPresentOrElse(
            r -> {LOG.log(Level.INFO, "USER ROLE NAME: {0}", r.getName());}, 
            () -> {
                Role userRole = new Role(RoleName.USER);
                roleDAO.makePersistent(userRole);
            }
        
        );
        
        aRole.ifPresentOrElse(
            r -> {LOG.log(Level.INFO, "ADMINISTRATOR ROLE NAME: {0}", r.getName());}, 
            () -> {
                Role adminRole = new Role(RoleName.ADMINISTRATOR);
                roleDAO.makePersistent(adminRole);
            }
        );
         
    }
    

    private void initAdminDefaultProperties(){
        createAdminUser();
        setAdminRole();
    }
    
    private void createAdminUser(){
        Optional<User> user = userDAO.findByLogin("admin@mutex.io");
        user.ifPresentOrElse(
            u -> {LOG.log(Level.INFO, "ROOT LOGIN: {0}",u.getLogin());}, 
            () -> {
                
                getAdminGroup().ifPresent(g -> doCreateAdminUser(g));
            }
        );
        
    }
    
    private Optional<Space> getAdminSpace(){
        return spaceDAO.findByName("mutex")
                .or(() -> {
                        Space space = new Space("mutex","admin sapce");
                        return spaceDAO.makePersistent(space);
                    }
               );
    }
    
        
    private Optional<Group> getAdminGroup(){
        Optional<Space> space = getAdminSpace();
        return space.flatMap(s -> groupDAO.findBySpaceAndName(s, "admin"))
                .or(() -> {
                    return space.map(s -> new Group("admin", s))
                              .flatMap(groupDAO::makePersistent);
                }
                
             );
    }
    
    private void doCreateAdminUser(Group group){
        Admin admin = new Admin("admin@mutex.io", EncryptionService.hash("root1234"),group);
        admin.setName("administrator");
        admin.setStatus(UserStatus.ENABLED);
        userDAO.makePersistent(admin);
    }
    
//    private Optional<Space> getAdminSpace(){
//        return spaceDAO.findByName("mutex")
//                .or(() -> {
//                        Space space = new Space("mutex");
//                        return spaceDAO.makePersistent(space);
//                    }
//               );
//    }
    
  
      
    private void setAdminRole(){
        Optional<Admin> admin = adminDAO.findByLogin(Constants.ADMIN_DEFAULT_LOGIN);
        Optional<Role> adminRole = roleDAO.findByName(RoleName.ADMINISTRATOR);

        Optional<UserRole> userRole= admin
                .flatMap(ru -> adminRole.flatMap(rr -> userRoleDAO.findByUserAndRole(ru.getLogin(),rr.getName())));
        
        if(userRole.isEmpty()){
            admin.flatMap(u -> adminRole.map(r -> new UserRole(u, r)))
                .ifPresent(ur -> userRoleDAO.makePersistent(ur));
        }
       
    }
   
    
}
