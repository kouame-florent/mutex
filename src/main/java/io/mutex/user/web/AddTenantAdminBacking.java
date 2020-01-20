/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.user.service.AdminUserService;
import io.mutex.user.service.TenantService;
import io.mutex.user.valueobject.ContextIdParamKey;

/**
 *
 * @author Florent
 */
@Named("addTenantAdminBacking")
@ViewScoped
public class AddTenantAdminBacking implements Serializable{

    private static final long serialVersionUID = 1L;

    @Inject AdminUserService adminUserService;
    @Inject TenantService tenantService;
    
    private AdminUser selectedAdminUser;
    private List<AdminUser> adminUsers = Collections.EMPTY_LIST;
    
    private Optional<Tenant> oCurrentTenant;
    private final ContextIdParamKey tenantParamKey = ContextIdParamKey.TENANT_UUID;
    private String tenantUUID;
    
    public void viewAction(){
    	oCurrentTenant = tenantService.findByUuid(tenantUUID);
        adminUsers = adminUserService.findNotAssignedToTenant();
   }
    
//    @PostConstruct
//    public void init(){
//    	
//    }
//    
    public void validate(){
    	Optional<AdminUser> oAdmin = oCurrentTenant.flatMap(t -> adminUserService.linkAdminUser(selectedAdminUser, t));
    	oAdmin.ifPresent(a -> PrimeFaces.current().dialog().closeDynamic(a));
    	 
    }
    
    public boolean rendererAction(AdminUser adminUser){
        return selectedAdminUser == adminUser;
    }
    
     
    public void check(AdminUser adminUser){   
       selectedAdminUser = adminUser;
        
    }
    

    public AdminUser getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public void setSelectedAdminUser(AdminUser selectedAdminUser) {
        this.selectedAdminUser = selectedAdminUser;
    }

    public List<AdminUser> getAdminUsers() {
        return adminUsers;
    }

	

	public ContextIdParamKey getTenantParamKey() {
		return tenantParamKey;
	}

	public String getTenantUUID() {
		return tenantUUID;
	}

	public void setTenantUUID(String tenantUUID) {
		this.tenantUUID = tenantUUID;
	}

    
}
