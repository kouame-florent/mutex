/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
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
        Optional<Role> role = roleDAO.findByName(RoleName.ROOT);
        if(!role.isPresent()){
            Role rootRole = new Role(RoleName.ROOT);
            Role userRole = new Role(RoleName.USER);
            Role adminRole = new Role(RoleName.ADMINISTRATOR);
              
            roleDAO.makePersistent(rootRole);
            roleDAO.makePersistent(adminRole);
            roleDAO.makePersistent(userRole);
            
        }
    }
    
    private void createTestTenant(){
        
        Optional<Tenant> stand = tenantDAO.findByName("stanford".toUpperCase());
        if(!stand.isPresent()){
           Tenant t1 = new Tenant("stanford".toUpperCase());
           Tenant t2 = new Tenant("princeton".toUpperCase());
           tenantDAO.makePersistent(t1);
           tenantDAO.makePersistent(t2);
        }
        
    }
    
    private void createTestGroup(){
        Optional<Tenant> stand = tenantDAO.findByName("stanford".toUpperCase());
        stand.ifPresent(tenant -> {  
            Optional<Group> histoire = groupDAO.findByTenantAndName(tenant, "Departement d'histoire");
            if(!histoire.isPresent()){
                Group group1 = new Group(tenant, "Departement d'histoire");
                Group group2 = new Group(tenant, "Departement d'économie");
                
                groupDAO.makePersistent(group1);
                groupDAO.makePersistent(group2);
                
            }
        });
        
        Optional<Tenant> princ = tenantDAO.findByName("princeton".toUpperCase());
        princ.ifPresent( tenant -> {   
            Optional<Group> math = groupDAO.findByTenantAndName(tenant, "Departement de math");
            if(!math.isPresent()){
                Group group1 = new Group(tenant, "Departement de math");
                Group group2 = new Group(tenant, "Departement de physique");
                Group group3 = new Group(tenant, "Departement d'informatique");
                
                groupDAO.makePersistent(group1);
                groupDAO.makePersistent(group2);
                groupDAO.makePersistent(group3);
           }
        });
    }
    
    
    
    private void createTestUser(){
        Optional<User> user = userDAO.findByLogin("sheldon@gmail.com");
        if(!user.isPresent()){
            Optional<Tenant> stanford = tenantDAO.findByName("stanford".toUpperCase());
            Optional<Tenant> princeton = tenantDAO.findByName("princeton".toUpperCase());
            
            AdminUser adminStanford = new AdminUser("admin.stanford@gmail.com",stanford.get());
            adminStanford.setName("Admin Stanford");
            adminStanford.setPassword(encryptionService.hash("admin1234"));
            adminStanford.setStatus(UserStatus.ENABLED);
             
            StandardUser user1 = new StandardUser("sheldon@gmail.com",stanford.get());
            user1.setName("Sheldon Cooper");
            user1.setPassword(encryptionService.hash("sheldon1234"));
            user1.setStatus(UserStatus.ENABLED);
            
            StandardUser user2 = new StandardUser("raj@gmail.com",stanford.get());
            user2.setName("Rajdish Koutrapali");
            user2.setPassword(encryptionService.hash("rajdish1234"));
            user2.setStatus(UserStatus.ENABLED);
                        
            StandardUser user3 = new StandardUser("howard@gmail.com",princeton.get());
            user3.setName("Howard Volowitz");
            user3.setPassword(encryptionService.hash("howard1234"));
            user3.setStatus(UserStatus.ENABLED);
            
            StandardUser user4 = new StandardUser("leonard@gmail.com",princeton.get());
            user4.setName("Leonard Hostaper");
            user4.setPassword(encryptionService.hash("leonard1234"));
            user4.setStatus(UserStatus.ENABLED);
            
            StandardUser user5 = new StandardUser("ami@gmail.com",princeton.get());
            user5.setName("Ami Farafoller");
            user5.setPassword(encryptionService.hash("ami12345"));
            user5.setStatus(UserStatus.ENABLED);
            
            StandardUser user6 = new StandardUser("kripke@gmail.com",stanford.get());
            user6.setName("Bari Kripke");
            user6.setPassword(encryptionService.hash("kripke1234"));
            user6.setStatus(UserStatus.ENABLED);
            
            userDAO.makePersistent(adminStanford);
            userDAO.makePersistent(user1);
            userDAO.makePersistent(user2);
            userDAO.makePersistent(user3);
            userDAO.makePersistent(user4);
            userDAO.makePersistent(user5);
            userDAO.makePersistent(user6);
        }
    }
    
    
    
    private void createTestUserGroup(){
        
        Optional<Tenant> stanford = tenantDAO.findByName("stanford".toUpperCase());
        Optional<Tenant> princeton = tenantDAO.findByName("princeton".toUpperCase());
        
        Optional<Group> stanfordHistoire = groupDAO.findByTenantAndName(stanford.get(), "Departement d'histoire");
        LOG.log(Level.INFO, "-->> GROUP: {0}", stanfordHistoire.get());
        Optional<Group> stanfordEconomie = groupDAO.findByTenantAndName(stanford.get(), "Departement d'économie");
        LOG.log(Level.INFO, "-->> GROUP: {0}", stanfordEconomie.get());
        
        Optional<Group> princetonMath = groupDAO.findByTenantAndName(princeton.get(), "Departement de math");
        Optional<Group> princetonPhysique = groupDAO.findByTenantAndName(princeton.get(), "Departement de physique");
        Optional<Group> princetonInformatique = groupDAO.findByTenantAndName(princeton.get(), "Departement d'informatique");
       
        Optional<User> sheldon = userDAO.findByLogin("sheldon@gmail.com");
        Optional<User> raj = userDAO.findByLogin("raj@gmail.com");
        Optional<User> kripke = userDAO.findByLogin("kripke@gmail.com");
        
        Optional<User> howard = userDAO.findByLogin("howard@gmail.com");
        Optional<User> leonard = userDAO.findByLogin("leonard@gmail.com");
        Optional<User> ami = userDAO.findByLogin("ami@gmail.com");
        
        UserGroup ug1 = new UserGroup(sheldon.get(), stanfordHistoire.get(),GroupType.PRIMARY);
        UserGroup ug7 = new UserGroup(kripke.get(), stanfordHistoire.get(),GroupType.PRIMARY);
        UserGroup ug2 = new UserGroup(raj.get(), stanfordEconomie.get(),GroupType.PRIMARY);
        
        UserGroup ug3 = new UserGroup(howard.get(), princetonMath.get(),GroupType.PRIMARY);
        
        UserGroup ug4 = new UserGroup(ami.get(), princetonInformatique.get(),GroupType.PRIMARY);
        
        UserGroup ug5 = new UserGroup(leonard.get(), princetonPhysique.get(),GroupType.PRIMARY);
        UserGroup ug6 = new UserGroup(ami.get(), princetonPhysique.get(),GroupType.SECONDARY);
        
        userGroupDAO.makePersistent(ug1);
        userGroupDAO.makePersistent(ug2);    
        userGroupDAO.makePersistent(ug3);
        userGroupDAO.makePersistent(ug4);
        userGroupDAO.makePersistent(ug5);
        userGroupDAO.makePersistent(ug6);
        userGroupDAO.makePersistent(ug7);
          
    }
    
    private void createTestUserRole(){
        Optional<User> adminStanford = userDAO.findByLogin("admin.stanford@gmail.com");
        Optional<User> sheldon = userDAO.findByLogin("sheldon@gmail.com");
        Optional<User> kripke = userDAO.findByLogin("kripke@gmail.com");
        Optional<User> raj = userDAO.findByLogin("raj@gmail.com");
        Optional<User> howard = userDAO.findByLogin("howard@gmail.com");
        Optional<User> leonard = userDAO.findByLogin("leonard@gmail.com");
        Optional<User> ami = userDAO.findByLogin("ami@gmail.com");
         
        Optional<Role> userRole = roleDAO.findByName(RoleName.USER);
        Optional<Role> adminRole = roleDAO.findByName(RoleName.ADMINISTRATOR);
         
        UserRole.Id id = new UserRole.Id(sheldon.get(), userRole.get());
        if(userRoleDAO.findById(id) == null){
            UserRole adminStanfordUser = new UserRole(adminStanford.get(), adminRole.get());
            UserRole sheldonUser = new UserRole(sheldon.get(), userRole.get());
            UserRole kripkeUser = new UserRole(kripke.get(), userRole.get());
            UserRole rajUser = new UserRole(raj.get(), userRole.get());
            UserRole howardUser = new UserRole(howard.get(), userRole.get());
            UserRole leonardUser = new UserRole(leonard.get(), userRole.get());
            UserRole amiUser = new UserRole(ami.get(), userRole.get());
            
            userRoleDAO.makePersistent(adminStanfordUser);
            userRoleDAO.makePersistent(sheldonUser);
            userRoleDAO.makePersistent(rajUser);
            userRoleDAO.makePersistent(howardUser);
            userRoleDAO.makePersistent(leonardUser);
            userRoleDAO.makePersistent(amiUser);
            userRoleDAO.makePersistent(kripkeUser);
        }
       
    }
    
    private void initRootDefaultProperties(){
        createRootUser();
        setRoleToRoot();
    }
    
    private void createRootUser(){
        Optional<User> user = userDAO.findByLogin("root@mutex.com");
        if(!user.isPresent()){
            RootUser root = new RootUser("root@mutex.com", null);
            root.setName("root");
            root.setPassword(encryptionService.hash("root1234"));
            root.setStatus(UserStatus.ENABLED);
           
            userDAO.makePersistent(root);
        }
    }
   
 
     
    private void setRoleToRoot(){
        Optional<User> root = userDAO.findByLogin("root@mutex.com");
        Optional<Role> rootRole = roleDAO.findByName(RoleName.ROOT);
        
        UserRole.Id id = new UserRole.Id(root.get(), rootRole.get());
        if(userRoleDAO.findById(id) == null){
            UserRole rootUser = new UserRole(root.get(), rootRole.get());
            userRoleDAO.makePersistent(rootUser);
        }
    }
}
