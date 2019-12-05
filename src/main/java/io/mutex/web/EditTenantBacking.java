/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.domain.entity.Tenant;
import io.mutex.service.TenantService;

/**
 *
 * @author Florent
 */
@Named(value = "editTenantBacking")
@ViewScoped
public class EditTenantBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(EditTenantBacking.class.getName());
      
    private final ViewParamKey tenantParamKey = ViewParamKey.TENANT_UUID;
    
    private String tenantUUID;
    
//    @Inject
//    private TenantDAO tenantDAO;
    
    @Inject
    private TenantService tenantService;
    
    private Tenant currentTenant;
    
    public void viewAction(){
         currentTenant = initTenant(tenantUUID);
    }
    
    private Tenant initTenant(String tenantUUID){
        return Optional.ofNullable(tenantUUID)
                .flatMap(tenantService::findByUuid)
                .orElseGet(() -> new Tenant());
     }
    
    public void processSaveTenant(){
        create(currentTenant).ifPresent(returnToCaller);
    }
    
    private Optional<Tenant> create(Tenant tenant){
        return tenantService.createTenant(tenant);
    }
    
//    Function<Tenant,Optional<Tenant>> save = ( Tenant t)  
//            -> tenantDAO.makePersistent(t);
//    
    Consumer<Tenant> returnToCaller = ( Tenant t) 
            -> PrimeFaces.current().dialog().closeDynamic(t);

    public String getTenantUUID() {
        return tenantUUID;
    }

    public void setTenantUUID(String tenantUUID) {
        this.tenantUUID = tenantUUID;
    }

    public ViewParamKey getTenantParamKey() {
        return tenantParamKey;
    }

    public Tenant getCurrentTenant() {
        return currentTenant;
    }

    public void setCurrentTenant(Tenant currentTenant) {
        this.currentTenant = currentTenant;
    }

}
