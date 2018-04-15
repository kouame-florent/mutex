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
import quantum.mutex.domain.Role;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.domain.UserStatus;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.RoleDAO;
import quantum.mutex.domain.dao.TenantDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;

/**
 *
 * @author Florent
 */
@Singleton
@Startup
public class Bootstrap {

    private static final Logger LOG = Logger.getLogger(Bootstrap.class.getName());
    
    
    @Inject FileIOService fileSservice;
    @Inject EncryptionService encryptionService;
    @Inject TenantDAO tenantDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserDAO userDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserGroupDAO userGroupDAO;
    
    @PostConstruct
    public void init(){
        fileSservice.createHomeDir();
        fileSservice.createSpoolDir();
        fileSservice.createStoreDir();
        fileSservice.createIndexDir();
        
        createTestTenant();
        createTestGroup();
        createTestRole();
        createTestUser();
        createTestUserGroup();
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
            Optional<Group> histoire = groupDAO.findByTenantAndName(tenant, "Departement d'histoire");
            if(!histoire.isPresent()){
                Group group1 = new Group(tenant, "Departement d'histoire");
                Group group2 = new Group(tenant, "Departement d'informatique");
                Group group3 = new Group(tenant, "Departement de microbiologie");
                
                groupDAO.makePersistent(group1);
                groupDAO.makePersistent(group2);
                groupDAO.makePersistent(group3);
           }
        });
    }
    
    public void createTestRole(){
        Optional<Role> role = roleDAO.findByName("user".toUpperCase());
        if(!role.isPresent()){
            Role role1 = new Role("user".toUpperCase());
            Role role2 = new Role("administrator".toUpperCase());
            Role role3 = new Role("root".toUpperCase());
            
            roleDAO.makePersistent(role1);
            roleDAO.makePersistent(role2);
            roleDAO.makePersistent(role3);
        }
    }
    
    private void createTestUser(){
        Optional<User> user = userDAO.findByLogin("sheldon@gmail.com");
        if(!user.isPresent()){
            Optional<Tenant> stanford = tenantDAO.findByName("stanford".toUpperCase());
            Optional<Tenant> princeton = tenantDAO.findByName("princeton".toUpperCase());
             
            User user1 = new User("sheldon@gmail.com",stanford.get());
            user1.setName("Sheldon Cooper");
            user1.setPassword(encryptionService.hash("sheldon"));
            user1.setStatus(UserStatus.ENABLED);
            
            User user2 = new User("raj@gmail.com",stanford.get());
            user2.setName("Rajdish Koutrapali");
            user2.setPassword(encryptionService.hash("raj"));
            user2.setStatus(UserStatus.ENABLED);
                        
            User user3 = new User("howard@gmail.com",princeton.get());
            user3.setName("Howard Volowitz");
            user3.setPassword(encryptionService.hash("howard"));
            user3.setStatus(UserStatus.ENABLED);
            
            User user4 = new User("leonard@gmail.com",princeton.get());
            user4.setName("Leonard Hostaper");
            user4.setPassword(encryptionService.hash("leonard"));
            user4.setStatus(UserStatus.ENABLED);
            
            User user5 = new User("ami@gmail.com",princeton.get());
            user5.setName("Ami Farafoller");
            user5.setPassword(encryptionService.hash("ami"));
            user5.setStatus(UserStatus.ENABLED);
            
            userDAO.makePersistent(user1);
            userDAO.makePersistent(user2);
            userDAO.makePersistent(user3);
            userDAO.makePersistent(user4);
            userDAO.makePersistent(user5);
        }
    }
    
    private void createTestUserGroup(){
        
        Optional<Tenant> stanford = tenantDAO.findByName("stanford".toUpperCase());
        Optional<Tenant> princeton = tenantDAO.findByName("princeton".toUpperCase());
        
        Optional<Group> histoireStan = groupDAO.findByTenantAndName(stanford.get(), "Departement d'histoire");
        LOG.log(Level.INFO, "-->> GROUP: {0}", histoireStan.get());
        Optional<Group> ecoStan = groupDAO.findByTenantAndName(stanford.get(), "Departement d'économie");
        LOG.log(Level.INFO, "-->> GROUP: {0}", ecoStan.get());
        
        Optional<Group> histoirePrinc = groupDAO.findByTenantAndName(princeton.get(), "Departement d'histoire");
        Optional<Group> infoPrinc = groupDAO.findByTenantAndName(princeton.get(), "Departement d'informatique");
        Optional<Group> microPrinc = groupDAO.findByTenantAndName(princeton.get(), "Departement de microbiologie");
       
        Optional<User> sheldon = userDAO.findByLogin("sheldon@gmail.com");
        Optional<User> raj = userDAO.findByLogin("raj@gmail.com");
        
        Optional<User> howard = userDAO.findByLogin("howard@gmail.com");
        Optional<User> leonard = userDAO.findByLogin("leonard@gmail.com");
        Optional<User> ami = userDAO.findByLogin("ami@gmail.com");
        
        UserGroup ug1 = new UserGroup(sheldon.get(), histoireStan.get());
        UserGroup ug2 = new UserGroup(raj.get(), ecoStan.get());
        
        UserGroup ug3 = new UserGroup(howard.get(), histoirePrinc.get());
        UserGroup ug4 = new UserGroup(leonard.get(), infoPrinc.get());
        UserGroup ug5 = new UserGroup(ami.get(), microPrinc.get());
        UserGroup ug6 = new UserGroup(ami.get(), infoPrinc.get());
        
        userGroupDAO.makePersistent(ug1);
        userGroupDAO.makePersistent(ug2);    
        userGroupDAO.makePersistent(ug3);
        userGroupDAO.makePersistent(ug4);
        userGroupDAO.makePersistent(ug5);
        userGroupDAO.makePersistent(ug6);
          
    }
}
