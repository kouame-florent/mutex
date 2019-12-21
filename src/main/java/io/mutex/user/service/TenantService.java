/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.valueobject.TenantStatus;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.exception.TenantNameExistException;
import io.mutex.user.repository.TenantDAO;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;


/**
 *
 * @author Florent
 */
@Stateless
public class TenantService{

    private static final Logger LOG = Logger.getLogger(TenantService.class.getName());
          
    @Inject TenantDAO tenantDAO;
    @Inject AdminUserService adminUserService;
        
    public List<Tenant> findAllTenants(){
       return tenantDAO.findAll();
    }
    
    public Optional<Tenant> findByName(String name){
        return tenantDAO.findByName(name.toUpperCase(Locale.getDefault()));
    }
    
    public Optional<Tenant> findByUuid(String uuid){
        return tenantDAO.findById(uuid);
    }
    
    public Optional<Tenant> createTenant(Tenant tenant) throws TenantNameExistException{
       var name = upperCaseWithoutAccent(tenant.getName());
       if(!isTenantWithNameExist(name)){
            return tenantDAO.makePersistent(nameToUpperCase(tenant));
        }
        throw new TenantNameExistException("Ce nom de tenant existe déjà");
    }
    
    public Optional<Tenant> updateTenant(Tenant tenant) throws TenantNameExistException {
        var name = upperCaseWithoutAccent(tenant.getName());
        Optional<Tenant> oTenantByName = tenantDAO.findByName(name);
       
        if((oTenantByName.isPresent() && oTenantByName.filter(t1 -> t1.equals(tenant)).isPresent()) ){
            return tenantDAO.makePersistent(nameToUpperCase(tenant));
        }
          
        if(!oTenantByName.isPresent()){
            return tenantDAO.makePersistent(nameToUpperCase(tenant));
        }
        throw new TenantNameExistException("Ce nom de tenant existe déjà");
    }
       
    private Tenant nameToUpperCase(Tenant tenant){
        String newName = upperCaseWithoutAccent(tenant.getName());
        LOG.log(Level.INFO, "[MUTEX] TENAT NAME: {0}", newName);
        tenant.setName(newName);
        return tenant;
    }
    
    private String upperCaseWithoutAccent(String name){
       String[] parts = removeAccent(name).map(StringUtils::split)
               .orElseGet(() -> new String[]{});
      return Arrays.stream(parts).map(StringUtils::strip).map(String::toUpperCase)
               .collect(Collectors.joining(" "));

    }
   
    private Optional<String> removeAccent(String name){
       return Optional.ofNullable(StringUtils.stripAccents(name));
    }
            
    public void deleteTenant(Tenant tenant){
         if(tenant != null){
            unlinkAdminAndChangeStatus(tenant);
            tenantDAO.makeTransient(tenant);     
        }
    }
    
    private void unlinkAdminAndChangeStatus(Tenant tenant){
        adminUserService.findByTenant(tenant)
                .flatMap(adminUserService::unlinkAdminUser)
                .ifPresent(adm -> adminUserService.changeAdminUserStatus(adm, UserStatus.DISABLED));
     }
    
    private boolean isTenantWithNameExist(String name){
        Optional<Tenant> oTenant = tenantDAO.findByName(name);
        return oTenant.isPresent();
    }
   
    public void updateTenantAdmin(Tenant tenant, AdminUser adminUser) throws AdminUserExistException, 
            NotMatchingPasswordAndConfirmation{
        tenantDAO.findById(tenant.getUuid())
                .ifPresent(this::unlinkAdminAndChangeStatus);
        updateCurrent(tenant, adminUser);
     }
    
    private Optional<AdminUser> updateCurrent(Tenant tenant, AdminUser adminUser) throws AdminUserExistException,
            NotMatchingPasswordAndConfirmation{
        adminUser.setTenant(tenant);
        return adminUserService.createAdminUser(adminUser);
    }
    
    public Tenant changeStatus(Tenant tenant,TenantStatus status){
        tenant.setStatus(status);
        return tenant;
    }

}
