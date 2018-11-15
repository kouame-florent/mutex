/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.util.function.Function;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import quantum.functional.api.Result;
import quantum.mutex.domain.Role;
import quantum.mutex.domain.RoleName;
import quantum.mutex.domain.RootUser;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserRole;
import quantum.mutex.domain.UserStatus;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.RoleDAO;
import quantum.mutex.domain.dao.TenantDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dao.UserRoleDAO;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Singleton
@Startup
public class ApplicationBootstrap {

    private static final Logger LOG = Logger.getLogger(ApplicationBootstrap.class.getName());
    
    
    @Inject FileIOService fileSservice;
    @Inject EncryptionService encryptionService;
    @Inject TenantDAO tenantDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserDAO userDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserRoleDAO userRoleDAO;
    
    @PostConstruct
    public void init(){
        fileSservice.createHomeDir();
        fileSservice.createSpoolDir();
        fileSservice.createStoreDir();
        fileSservice.createIndexDir();
      
        createDefaultRoles();
        initRootDefaultProperties();
        
       // createTestTenant();
       //  createTestGroup();
       //  createTestUser();
      //  createTestUserGroup();
      //  createTestUserRole();
        
        
    }
    
    
    public void createDefaultRoles(){
        Result<Role> rRole = roleDAO.findByName(RoleName.ROOT);
        Result<Role> uRole = roleDAO.findByName(RoleName.USER);
        Result<Role> aRole = roleDAO.findByName(RoleName.ADMINISTRATOR);
        
        rRole.orElse(() -> {
            Role rootRole = new Role(RoleName.ROOT);
            return roleDAO.makePersistent(rootRole);
        });
        
        uRole.orElse(() -> {
            Role userRole = new Role(RoleName.USER);
            return roleDAO.makePersistent(userRole);
        });
        
        aRole.orElse(() -> {
            Role adminRole = new Role(RoleName.ADMINISTRATOR);
            return roleDAO.makePersistent(adminRole);
        });
        
    }
    

    private void initRootDefaultProperties(){
        createRootUser();
        setRoleToRoot();
    }
    
    private void createRootUser(){
        Result<User> user = userDAO.findByLogin("root@mutex.com");
        user.orElse(() -> {
            RootUser root = new RootUser("root@mutex.com", null);
            root.setName("root");
            root.setPassword(encryptionService.hash("root1234"));
            root.setStatus(UserStatus.ENABLED);
            return userDAO.makePersistent(root);
        });
        
    }
   
 
     
    private void setRoleToRoot(){
        Result<User> root = userDAO.findByLogin(Constants.ROOT_DEFAULT_LOGIN);
        Result<Role> rootRole = roleDAO.findByName(RoleName.ROOT);
        
        UserRole.Id id = new UserRole.Id(root.getOrElse(new User()), 
                rootRole.getOrElse(new Role()));
        
        if( userRoleDAO.findById(id).isEmpty()){
            root.flatMap(u -> rootRole.map(r -> createUserRole.apply(u).apply(r)))
                .forEach(ur -> userRoleDAO.makePersistent(ur));
        }
        
    }
    
    private final Function<User,Function<Role,UserRole>> createUserRole = u -> r -> {
        return new UserRole(u, r);
    }; 
    
    
}
