/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.shared.service;


import mutex.index.service.FileIOService;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import mutex.user.domain.entity.Role;
import mutex.user.domain.valueobject.RoleName;
import mutex.user.domain.entity.RootUser;
import mutex.user.domain.entity.User;
import mutex.user.domain.entity.UserRole;
import mutex.user.domain.valueobject.UserStatus;
import mutex.user.repository.GroupDAO;
import mutex.user.repository.RoleDAO;
import mutex.user.repository.TenantDAO;
import mutex.user.repository.UserDAO;
import mutex.user.repository.UserGroupDAO;
import mutex.user.repository.UserRoleDAO;
import mutex.user.domain.entity.Tenant;
import mutex.util.Constants;


/**
 *
 * @author Florent
 */
@Singleton
@Startup
public class ApplicationBootstrap {

    private static final Logger LOG = Logger.getLogger(ApplicationBootstrap.class.getName());
    
    
    @Inject FileIOService fileService;
    @Inject TenantDAO tenantDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserDAO userDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserRoleDAO userRoleDAO;
    
    @PostConstruct
    public void init(){
        fileService.createHomeDir();
//        fileSservice.createSpoolDir();
        fileService.createStoreDir();
        fileService.createIndexDir();
      
        createDefaultRoles();
        initRootDefaultProperties();
      
    }
    
    
    public void createDefaultRoles(){
        Optional<Role> rRole = roleDAO.findByName(RoleName.ROOT);
        Optional<Role> uRole = roleDAO.findByName(RoleName.USER);
        Optional<Role> aRole = roleDAO.findByName(RoleName.ADMINISTRATOR);
        
        rRole.ifPresentOrElse(
            r -> {LOG.log(Level.INFO, "ROOT ROLE NAME: {0}", r.getName());}, 
            () -> {
                Role rootRole = new Role(RoleName.ROOT);
                roleDAO.makePersistent(rootRole);
            }
        );
        
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
    

    private void initRootDefaultProperties(){
        createRootUser();
        setRoleToRoot();
    }
    
    private void createRootUser(){
        Optional<User> user = userDAO.findByLogin("root@mutex.com");
        user.ifPresentOrElse(
            u -> {LOG.log(Level.INFO, "ROOT LOGIN: {0}",u.getLogin());}, 
            () -> {
                RootUser root = new RootUser("root@mutex.com", null);
                root.setName("root");
                root.setPassword(EncryptionService.hash("root1234"));
                root.setStatus(UserStatus.ENABLED);
                getRootTenant().ifPresent(t -> root.setTenant(t));
                userDAO.makePersistent(root);

            }
        );
        
    }
    
    private Optional<Tenant> getRootTenant(){
        return tenantDAO.findByName("mutex.io")
                .or(() -> {
                        Tenant tenant = new Tenant("mutex");
                        return tenantDAO.makePersistent(tenant);
                    }
               );
    }
      
    private void setRoleToRoot(){
        Optional<User> rootUser = userDAO.findByLogin(Constants.ROOT_DEFAULT_LOGIN);
        Optional<Role> rootRole = roleDAO.findByName(RoleName.ROOT);

        Optional<UserRole> userRole= rootUser
                .flatMap(ru -> rootRole.flatMap(rr -> userRoleDAO.findByUserAndRole(ru.getLogin(),rr.getName())));
        
        if(userRole.isEmpty()){
            rootUser.flatMap(u -> rootRole.map(r -> createUserRole.apply(u).apply(r)))
                .ifPresent(ur -> userRoleDAO.makePersistent(ur));
        }
       
    }
    
    private final Function<User,Function<Role,UserRole>> createUserRole = u -> r -> {
        return new UserRole(u, r);
    }; 
    
    
}
