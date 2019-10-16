/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.service;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.user.domain.entity.AdminUser;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.domain.valueobject.UserStatus;
import quantum.mutex.user.repository.AdminUserDAO;
import quantum.mutex.user.repository.TenantDAO;


/**
 *
 * @author Florent
 */
@Stateless
public class TenantService {

    private static final Logger LOG = Logger.getLogger(TenantService.class.getName());
    
    
    
    @Inject TenantDAO tenantDAO;
    @Inject AdminUserDAO adminUserDAO;
    
    public Optional<Tenant> findByName(String name){
        return tenantDAO.findByName(name.toUpperCase(Locale.getDefault()));
    }
    
    public Optional<Tenant> findByUuid(String uuid){
        return tenantDAO.findById(uuid);
    }
    
    public Optional<Tenant> createTenant(Tenant tenant){
//        tenant.setName(tenant.getName().toUpperCase());
//        if(!isTenantExist(tenant)){
//           return tenantDAO.makePersistent(tenant);
//        }
       
//       return Optional.empty();

        if(!isTenantWithNameExist(tenant.getName())){
            return tenantDAO.makePersistent(tenant);
        }
        
        return Optional.empty();
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
       optMngTenant.map(adminUserDAO::findByTenant).orElseGet(ArrayList::new)
               .stream().forEach(this::updatePrevious);
   }
    
    private Optional<AdminUser> updatePrevious(AdminUser adminUser){
        adminUser.setStatus(UserStatus.DISABLED);
        adminUser.setTenant(null);
        return adminUserDAO.makePersistent(adminUser);
    }
    
    private Optional<AdminUser> updateCurrent(Tenant tenant, AdminUser adminUser){
        adminUser.setTenant(tenant);
        return adminUserDAO.makePersistent(adminUser);
    }
    
}
