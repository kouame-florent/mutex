/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web.user;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.domain.entity.Tenant;
import io.mutex.service.user.TenantService;
import io.mutex.web.ViewParamKey;

/**
 *
 * @author Florent
 */
@Named(value = "editTenantBacking")
@ViewScoped
public class EditTenantBacking extends EditBacking<Tenant> implements Serializable{
     
    private final ViewParamKey tenantParamKey = ViewParamKey.TENANT_UUID;
    
    @Inject
    private TenantService tenantService;
    
    private Tenant currentTenant;
    
    @Override
    public void viewAction(){
         currentTenant = initEntity(entityUUID);
         viewState = initViewState(entityUUID);
    }
    
    @Override
    protected Tenant initEntity(String entityUUID) {
        return Optional.ofNullable(entityUUID)
                .flatMap(tenantService::findByUuid)
                .orElseGet(() -> new Tenant());
    }

    @Override
    public void persistEntity() {
         switch(viewState){
            case CREATE:
                tenantService.createTenant(currentTenant).ifPresent(returnToCaller);
                break;
            case UPDATE:
                tenantService.updateTenant(currentTenant).ifPresent(returnToCaller);
                break;
        }

    }
        
    Consumer<Tenant> returnToCaller = (Tenant t) -> PrimeFaces.current().dialog().closeDynamic(t);

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
