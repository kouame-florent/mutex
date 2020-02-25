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
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.exception.TenantNameExistException;
import io.mutex.user.repository.TenantDAO;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;


/**
 *
 * @author Florent
 */
@Stateless
public class SpaceServiceImpl implements SpaceService{

    private static final Logger LOG = Logger.getLogger(SpaceServiceImpl.class.getName());
          
    @Inject TenantDAO tenantDAO;
    @Inject AdminService adminUserService;
        
    @Override
    public List<Space> findAllTenants(){
       return tenantDAO.findAll().stream()
               .filter(t -> !t.getName().equalsIgnoreCase("mutex"))
               .collect(toList());
    }
    
    @Override
    public Optional<Space> findByName(@NotBlank String name){
        return tenantDAO.findByName(name.toUpperCase(Locale.getDefault()));
    }
      
    @Override
    public Optional<Space> findByUuid(@NotBlank String uuid){
        return tenantDAO.findById(uuid);
    }
       
    @Override
    public Optional<Space> create(@NotNull Space tenant) throws TenantNameExistException{
       var name = upperCaseWithoutAccent(tenant.getName());
       if(!isTenantWithNameExist(name)){
            return tenantDAO.makePersistent(nameToUpperCase(tenant));
        }
        throw new TenantNameExistException("Ce nom de tenant existe déjà");
    }
      
    @Override
    public Optional<Space> update(@NotNull Space tenant) throws TenantNameExistException {
        var name = upperCaseWithoutAccent(tenant.getName());
        Optional<Space> oTenantByName = tenantDAO.findByName(name);
       
        if((oTenantByName.isPresent() && oTenantByName.filter(t1 -> t1.equals(tenant)).isEmpty()) ){
            throw new TenantNameExistException("Ce nom de tenant existe déjà");
        }
        return tenantDAO.makePersistent(nameToUpperCase(tenant));
    }
    
    private boolean isTenantWithNameExist(@NotBlank String name){
        Optional<Space> oTenant = tenantDAO.findByName(name);
        return oTenant.isPresent();
    }
       
    private Space nameToUpperCase(@NotNull Space tenant){
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
    
    @Override
    public void delete(@NotNull Space tenant){
        unlinkAdminAndChangeStatus(tenant);
        tenantDAO.makeTransient(tenant);     
    }
    
    @Override
    public void unlinkAdminAndChangeStatus(@NotNull Space tenant){
        adminUserService.findBySpace(tenant)
//                .flatMap(adminUserService::unlinkAdminUser)
                .ifPresent(adm -> adminUserService.changeAdminUserStatus(adm, UserStatus.DISABLED));
    }
  
    @Override
    public void updateTenantAdmin(@NotNull Space tenant, @NotNull Admin adminUser) 
            throws AdminUserExistException, 
            NotMatchingPasswordAndConfirmation{
        tenantDAO.findById(tenant.getUuid())
                .ifPresent(this::unlinkAdminAndChangeStatus);
        updateTenantAdmin_(tenant, adminUser);
     }
    
    private Optional<Admin> updateTenantAdmin_(@NotNull Space tenant, @NotNull Admin adminUser) 
            throws AdminUserExistException,
            NotMatchingPasswordAndConfirmation{
//        adminUser.setTenant(tenant);
        return adminUserService.createAdminUser(adminUser);
    }
    
    @Override
    public Space changeStatus(@NotNull Space tenant,@NotNull TenantStatus status){
        tenant.setStatus(status);
        return tenant;
    }

}
