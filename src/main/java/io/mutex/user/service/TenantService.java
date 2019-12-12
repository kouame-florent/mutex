/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.search.valueobject.TenantStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.search.valueobject.UserStatus;
import io.mutex.user.repository.TenantDAO;


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
    
    public Optional<Tenant> createTenant(Tenant tenant){
       if(!isTenantWithNameExist(tenant.getName())){
            return tenantDAO.makePersistent(tenant);
        }
        return Optional.empty();
    }
    
    public Optional<Tenant> updateTenant(Tenant tenant){
        return tenantDAO.makePersistent(tenant);
    }
    
    public void deleteTenant(Tenant tenant){
         if(tenant != null){
            changeAdminStatus(tenant);
            tenantDAO.makeTransient(tenant);     
        }
       
//        tenantDAO.makeTransient(tenant);
    }
    
    private void changeAdminStatus(Tenant tenant){
        adminUserService.findByTenant(tenant)
                .stream().forEach(adminUserService::changeAdminUserStatus);
    }
    
    private boolean isTenantWithNameExist(String name){
        Optional<Tenant> oTenant = tenantDAO.findByName(name);
        return oTenant.isPresent();
    }
        
    public void updateTenantAdmin( Tenant tenant, AdminUser adminUser){
        resetPreviousAdmin(tenant.getUuid());
        updateCurrent(tenant, adminUser);
     }
    
    private void resetPreviousAdmin( String uuid){
       Optional<Tenant> optMngTenant = tenantDAO.findById(uuid);
       optMngTenant.map(adminUserService::findByTenant).orElseGet(ArrayList::new)
               .stream().forEach(this::updatePrevious);
    }
    
    private Optional<AdminUser> updatePrevious(AdminUser adminUser){
        adminUser.setStatus(UserStatus.DISABLED);
        adminUser.setTenant(null);
        return adminUserService.createAdminUser(adminUser);

    }
    
    private Optional<AdminUser> updateCurrent(Tenant tenant, AdminUser adminUser){
        adminUser.setTenant(tenant);
        return adminUserService.createAdminUser(adminUser);
    }
    
    public void disableTenant(Tenant tenant){
        tenant.setStatus(TenantStatus.DISABLED);
    }

   
}
