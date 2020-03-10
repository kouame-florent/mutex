/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.shared.service;


import io.mutex.index.service.FileIOServiceImpl;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import io.mutex.user.entity.Role;
import io.mutex.user.valueobject.RoleName;
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
import io.mutex.user.entity.UserGroup;
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
    
    
    @Inject FileIOServiceImpl fileService;
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
        createAdminDefaultObjects();
        createDefaultObjects();
      
    }
        
    public void createDefaultRoles(){
        Optional<Role> uRole = roleDAO.findByName(RoleName.USER);
        Optional<Role> aRole = roleDAO.findByName(RoleName.ADMINISTRATOR);

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
    
    private void createDefaultObjects(){
      createDefaultSpace();
      createDefaultGroup();
    }
    
    private void createDefaultSpace(){
        Optional<Space> space = spaceDAO.findByName(Constants.DEFAULT_SPACE);
        space.ifPresentOrElse(s -> {LOG.log(Level.INFO, "DEFAULT SPACE: {0}",s);}, 
                () -> {
                    Space spc = new Space(Constants.DEFAULT_SPACE, "Espace par défaut");
                    spaceDAO.makePersistent(spc);
                });
    }
    
    private void createDefaultGroup(){
        Optional<Space> space = spaceDAO.findByName(Constants.DEFAULT_SPACE);
        Optional<Group> group = space.flatMap(s -> groupDAO.findBySpaceAndName(s, Constants.DEFAULT_GROUP));
        group.ifPresentOrElse(g -> {LOG.log(Level.INFO, "DEFAULT GROUP: {0}",g);}, 
                () -> {
                    space.map(s -> new Group(Constants.DEFAULT_GROUP, s,"Groupe par defaut"))
                            .ifPresent(g -> groupDAO.makePersistent(g));
                    
                });
    }
    
    private void createAdminDefaultObjects(){
        createAdmin();
        createAdminRole();
        createAdminGroup();
    }
    
    private void createAdmin(){
        Optional<User> user = userDAO.findByLogin("admin@mutex.io");
        user.ifPresentOrElse(
            u -> {LOG.log(Level.INFO, "ADMIN LOGIN: {0}",u.getLogin());}, 
            () -> {
                
                getAdminGroup().ifPresent(g -> doCreateAdmin(g));
            }
        );
        
    }
    
    private void createAdminGroup(){
        
        Optional<Admin> admin = adminDAO.findByLogin(Constants.ADMIN_DEFAULT_LOGIN);
        Optional<Space> space = getAdminSpace();
        Optional<Group> group = space
                .flatMap(s -> groupDAO.findBySpaceAndName(s, Constants.ADMIN_DEFAULT_SPACE));
        
        admin.flatMap(a -> group.flatMap(g -> userGroupDAO.findByUserAndGroup(a, g)))
                .ifPresentOrElse(
                        ug -> {}, 
                        () -> {
                            admin.flatMap(a -> group.map(g -> new UserGroup(a, g)))
                                .ifPresent(ug -> userGroupDAO.makePersistent(ug));
                        }
                );
    }
    
     private void createAdminRole(){
        Optional<Admin> admin = adminDAO.findByLogin(Constants.ADMIN_DEFAULT_LOGIN);
        Optional<Role> adminRole = roleDAO.findByName(RoleName.ADMINISTRATOR);

        Optional<UserRole> userRole= admin
                .flatMap(ru -> adminRole.flatMap(rr -> userRoleDAO.findByUserAndRole(ru.getLogin(),rr.getName())));
        
        if(userRole.isEmpty()){
            admin.flatMap(u -> adminRole.map(r -> new UserRole(u, r)))
                .ifPresent(ur -> userRoleDAO.makePersistent(ur));
        }
       
    }
    
    private Optional<Space> getAdminSpace(){
        return spaceDAO.findByName("mutex")
                .or(() -> {
                        Space space = new Space(Constants.ADMIN_DEFAULT_SPACE,"admin space");
                        return spaceDAO.makePersistent(space);
                    }
               );
    }
    
        
    private Optional<Group> getAdminGroup(){
        Optional<Space> space = getAdminSpace();
        return space.flatMap(s -> groupDAO.findBySpaceAndName(s, Constants.ADMIN_DEFAULT_SPACE))
                .or(() -> {
                    return space.map(s -> new Group(Constants.ADMIN_DEFAULT_SPACE, s,"Groupe de l'administrateur"))
                              .flatMap(groupDAO::makePersistent);
                }
                
            );
    }
    
    
    private void doCreateAdmin(Group group){
        Admin admin = new Admin(Constants.ADMIN_DEFAULT_LOGIN, 
                EncryptionService.hash(Constants.ADMIN_DEFAULT_PASSWD));
        admin.setName("administrator");
        admin.setStatus(UserStatus.ENABLED);
        userDAO.makePersistent(admin);
    }
    
}
