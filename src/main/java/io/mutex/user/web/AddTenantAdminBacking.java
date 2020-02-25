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
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.service.AdminService;
import io.mutex.user.service.SpaceService;

/**
 *
 * @author Florent
 */
@Named("addTenantAdminBacking")
@ViewScoped
public class AddTenantAdminBacking implements Serializable{

    private static final long serialVersionUID = 1L;

    @Inject AdminService adminUserService;
    @Inject SpaceService tenantService;
    
    private Admin selectedAdminUser;
    private List<Admin> adminUsers = Collections.EMPTY_LIST;
    
    private Optional<Space> oCurrentTenant;
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
    	Optional<Admin> oAdmin = oCurrentTenant.flatMap(t -> adminUserService.linkAdminUser(selectedAdminUser, t));
    	oAdmin.ifPresent(a -> PrimeFaces.current().dialog().closeDynamic(a));
    	 
    }
    
    public boolean rendererAction(Admin adminUser){
        return selectedAdminUser == adminUser;
    }
    
     
    public void check(Admin adminUser){   
       selectedAdminUser = adminUser;
        
    }
    

    public Admin getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public void setSelectedAdminUser(Admin selectedAdminUser) {
        this.selectedAdminUser = selectedAdminUser;
    }

    public List<Admin> getAdminUsers() {
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
