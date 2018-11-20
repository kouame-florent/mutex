/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.AdminUser;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.entity.UserStatus;
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
    
    
    public void updateTenantAdmin(@NotNull Tenant tenant,@NotNull AdminUser adminUser){
        resetPreviousAdmin(tenant.getUuid());
        updateCurrent(tenant, adminUser);
     }
    
    private void resetPreviousAdmin(@NotNull UUID uuid){
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
