/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.GroupType;
import quantum.mutex.domain.Role;
import quantum.mutex.domain.StandardUser;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.RoleName;
import quantum.mutex.domain.RootUser;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.domain.UserRole;
import quantum.mutex.domain.UserStatus;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.RoleDAO;
import quantum.mutex.domain.dao.TenantDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dao.UserRoleDAO;

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
        Result<Role> uRole = roleDAO.findByName(RoleName.ROOT);
        Result<Role> aRole = roleDAO.findByName(RoleName.ROOT);
        
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
    

    
//    private void createTestUserGroup(){
//        
//        Optional<Tenant> stanford = tenantDAO.findByName("stanford".toUpperCase());
//        Optional<Tenant> princeton = tenantDAO.findByName("princeton".toUpperCase());
//        
//        Optional<Group> stanfordHistoire = groupDAO.findByTenantAndName(stanford.get(), "Departement d'histoire");
//        LOG.log(Level.INFO, "-->> GROUP: {0}", stanfordHistoire.get());
//        Optional<Group> stanfordEconomie = groupDAO.findByTenantAndName(stanford.get(), "Departement d'économie");
//        LOG.log(Level.INFO, "-->> GROUP: {0}", stanfordEconomie.get());
//        
//        Optional<Group> princetonMath = groupDAO.findByTenantAndName(princeton.get(), "Departement de math");
//        Optional<Group> princetonPhysique = groupDAO.findByTenantAndName(princeton.get(), "Departement de physique");
//        Optional<Group> princetonInformatique = groupDAO.findByTenantAndName(princeton.get(), "Departement d'informatique");
//       
//        Optional<User> sheldon = userDAO.findByLogin("sheldon@gmail.com");
//        Optional<User> raj = userDAO.findByLogin("raj@gmail.com");
//        Optional<User> kripke = userDAO.findByLogin("kripke@gmail.com");
//        
//        Optional<User> howard = userDAO.findByLogin("howard@gmail.com");
//        Optional<User> leonard = userDAO.findByLogin("leonard@gmail.com");
//        Optional<User> ami = userDAO.findByLogin("ami@gmail.com");
//        
//        UserGroup ug1 = new UserGroup(sheldon.get(), stanfordHistoire.get(),GroupType.PRIMARY);
//        UserGroup ug7 = new UserGroup(kripke.get(), stanfordHistoire.get(),GroupType.PRIMARY);
//        UserGroup ug2 = new UserGroup(raj.get(), stanfordEconomie.get(),GroupType.PRIMARY);
//        
//        UserGroup ug3 = new UserGroup(howard.get(), princetonMath.get(),GroupType.PRIMARY);
//        
//        UserGroup ug4 = new UserGroup(ami.get(), princetonInformatique.get(),GroupType.PRIMARY);
//        
//        UserGroup ug5 = new UserGroup(leonard.get(), princetonPhysique.get(),GroupType.PRIMARY);
//        UserGroup ug6 = new UserGroup(ami.get(), princetonPhysique.get(),GroupType.SECONDARY);
//        
//        userGroupDAO.makePersistent(ug1);
//        userGroupDAO.makePersistent(ug2);    
//        userGroupDAO.makePersistent(ug3);
//        userGroupDAO.makePersistent(ug4);
//        userGroupDAO.makePersistent(ug5);
//        userGroupDAO.makePersistent(ug6);
//        userGroupDAO.makePersistent(ug7);
//          
//    }
    
//    private void createTestUserRole(){
//        Optional<User> adminStanford = userDAO.findByLogin("admin.stanford@gmail.com");
//        Optional<User> sheldon = userDAO.findByLogin("sheldon@gmail.com");
//        Optional<User> kripke = userDAO.findByLogin("kripke@gmail.com");
//        Optional<User> raj = userDAO.findByLogin("raj@gmail.com");
//        Optional<User> howard = userDAO.findByLogin("howard@gmail.com");
//        Optional<User> leonard = userDAO.findByLogin("leonard@gmail.com");
//        Optional<User> ami = userDAO.findByLogin("ami@gmail.com");
//         
//        Optional<Role> userRole = roleDAO.findByName(RoleName.USER);
//        Optional<Role> adminRole = roleDAO.findByName(RoleName.ADMINISTRATOR);
//         
//        UserRole.Id id = new UserRole.Id(sheldon.get(), userRole.get());
//        if(userRoleDAO.findById(id) == null){
//            UserRole adminStanfordUser = new UserRole(adminStanford.get(), adminRole.get());
//            UserRole sheldonUser = new UserRole(sheldon.get(), userRole.get());
//            UserRole kripkeUser = new UserRole(kripke.get(), userRole.get());
//            UserRole rajUser = new UserRole(raj.get(), userRole.get());
//            UserRole howardUser = new UserRole(howard.get(), userRole.get());
//            UserRole leonardUser = new UserRole(leonard.get(), userRole.get());
//            UserRole amiUser = new UserRole(ami.get(), userRole.get());
//            
//            userRoleDAO.makePersistent(adminStanfordUser);
//            userRoleDAO.makePersistent(sheldonUser);
//            userRoleDAO.makePersistent(rajUser);
//            userRoleDAO.makePersistent(howardUser);
//            userRoleDAO.makePersistent(leonardUser);
//            userRoleDAO.makePersistent(amiUser);
//            userRoleDAO.makePersistent(kripkeUser);
//        }
//       
//    }
//    
    private void initRootDefaultProperties(){
        createRootUser();
        setRoleToRoot();
    }
    
    private void createRootUser(){
        Result<User> user = userDAO.findByLogin("root@mutex.com");
        user.forEach(u -> {
             RootUser root = new RootUser("root@mutex.com", null);
            root.setName("root");
            root.setPassword(encryptionService.hash("root1234"));
            root.setStatus(UserStatus.ENABLED);
           
            userDAO.makePersistent(root);
        });
        
    }
   
 
     
    private void setRoleToRoot(){
        Result<User> root = userDAO.findByLogin("root@mutex.com");
        Result<Role> rootRole = roleDAO.findByName(RoleName.ROOT);
        
        UserRole.Id id = new UserRole.Id(root.getOrElse(new User()), 
                rootRole.getOrElse(new Role()));
        
        userRoleDAO.findById(id).filter(ur -> ur == null)
                .forEach(ur -> {
                    UserRole newUserRole = new UserRole(root.getOrElse(new User()), 
                            rootRole.getOrElse(new Role()));
                    userRoleDAO.makePersistent(ur);
                });
      
    }
    
    
}
