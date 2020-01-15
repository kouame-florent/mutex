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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    
    public Optional<Tenant> findByName(@NotBlank String name){
        return tenantDAO.findByName(name.toUpperCase(Locale.getDefault()));
    }
    
    public Optional<Tenant> findByUuid(@NotBlank String uuid){
        return tenantDAO.findById(uuid);
    }
    
    public Optional<Tenant> create(@NotNull Tenant tenant) throws TenantNameExistException{
       var name = upperCaseWithoutAccent(tenant.getName());
       if(!isTenantWithNameExist(name)){
            return tenantDAO.makePersistent(nameToUpperCase(tenant));
        }
        throw new TenantNameExistException("Ce nom de tenant existe déjà");
    }
    
    public Optional<Tenant> update(@NotNull Tenant tenant) throws TenantNameExistException {
        var name = upperCaseWithoutAccent(tenant.getName());
        Optional<Tenant> oTenantByName = tenantDAO.findByName(name);
       
        if((oTenantByName.isPresent() && oTenantByName.filter(t1 -> t1.equals(tenant)).isEmpty()) ){
            throw new TenantNameExistException("Ce nom de tenant existe déjà");
        }
        return tenantDAO.makePersistent(nameToUpperCase(tenant));
    }
    
    private boolean isTenantWithNameExist(@NotBlank String name){
        Optional<Tenant> oTenant = tenantDAO.findByName(name);
        return oTenant.isPresent();
    }
       
    private Tenant nameToUpperCase(@NotNull Tenant tenant){
        String newName = upperCaseWithoutAccent(tenant.getName());
        LOG.log(Level.INFO, "[MUTEX] TENAT NAME: {0}", newName);
        tenant.setName(newName);
        return tenant;
    }
    
    private String upperCaseWithoutAccent(@NotBlank String name){
       String[] parts = removeAccent(name).map(StringUtils::split)
               .orElseGet(() -> new String[]{});
      return Arrays.stream(parts).map(StringUtils::strip).map(String::toUpperCase)
               .collect(Collectors.joining(" "));
    }
   
    private Optional<String> removeAccent(@NotBlank String name){
       return Optional.ofNullable(StringUtils.stripAccents(name));
    }
            
    public void delete(@NotNull Tenant tenant){
        unlinkAdminAndChangeStatus(tenant);
        tenantDAO.makeTransient(tenant);     
    }
    
    public void unlinkAdminAndChangeStatus(@NotNull Tenant tenant){
        adminUserService.findByTenant(tenant)
                .flatMap(adminUserService::unlinkAdminUser)
                .ifPresent(adm -> adminUserService.changeAdminUserStatus(adm, UserStatus.DISABLED));
    }
  
    public void updateTenantAdmin(@NotNull Tenant tenant, @NotNull AdminUser adminUser) 
            throws AdminUserExistException, 
            NotMatchingPasswordAndConfirmation{
        tenantDAO.findById(tenant.getUuid())
                .ifPresent(this::unlinkAdminAndChangeStatus);
        updateTenantAdmin_(tenant, adminUser);
     }
    
    private Optional<AdminUser> updateTenantAdmin_(@NotNull Tenant tenant, @NotNull AdminUser adminUser) 
            throws AdminUserExistException,
            NotMatchingPasswordAndConfirmation{
        adminUser.setTenant(tenant);
        return adminUserService.createAdminUser(adminUser);
    }
    
    public Tenant changeStatus(@NotNull Tenant tenant,@NotNull TenantStatus status){
        tenant.setStatus(status);
        return tenant;
    }

}
