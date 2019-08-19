/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.domain;

import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.entity.AdminUser;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.entity.UserStatus;
import quantum.mutex.domain.dao.AdminUserDAO;
import quantum.mutex.domain.dao.TenantDAO;
import quantum.mutex.util.functional.Result;

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
       Result<Tenant> optMngTenant = tenantDAO.findById(uuid);
       optMngTenant.map(adminUserDAO::findByTenant).getOrElse(ArrayList::new)
               .stream().forEach(this::updatePrevious);
   }
    
    private Result<AdminUser> updatePrevious(AdminUser adminUser){
        adminUser.setStatus(UserStatus.DISABLED);
        adminUser.setTenant(null);
        return adminUserDAO.makePersistent(adminUser);
    }
    
    private Result<AdminUser> updateCurrent(Tenant tenant, AdminUser adminUser){
        adminUser.setTenant(tenant);
        return adminUserDAO.makePersistent(adminUser);
    }
    
}
