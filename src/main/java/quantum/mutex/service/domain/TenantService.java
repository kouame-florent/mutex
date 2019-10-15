/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.domain;

import java.util.ArrayList;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.user.domain.entity.AdminUser;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.domain.valueobject.UserStatus;
import quantum.mutex.domain.dao.AdminUserDAO;
import quantum.mutex.domain.dao.TenantDAO;


/**
 *
 * @author Florent
 */
@Stateless
public class TenantService {
    
    @Inject TenantDAO tenantDAO;
    @Inject AdminUserDAO adminUserDAO;
    
    
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
