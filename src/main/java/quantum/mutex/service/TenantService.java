/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.UserStatus;
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
    
    
    public void updateTenantAdmin(Tenant tenant,AdminUser adminUser){
        AdminUser mAdminUser = adminUserDAO.findById(adminUser.getUuid());
        Tenant mTenant = tenantDAO.findById(tenant.getUuid());
        resetPreviousAdmin(mTenant);
        mAdminUser.setTenant(mTenant);
        mAdminUser.setStatus(UserStatus.ENABLED);
       
    }
    
    private void resetPreviousAdmin(Tenant tenant){
       List<AdminUser> adminUsers = adminUserDAO.findByTenant(tenant);
       if(!adminUsers.isEmpty()){
           adminUsers.forEach(a -> {
               a.setStatus(UserStatus.DISABLED);
               a.setTenant(null);
               adminUserDAO.makePersistent(a);
           } );
       }
    }
    
}
