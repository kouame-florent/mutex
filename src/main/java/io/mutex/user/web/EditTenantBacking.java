/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.Optional;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.entity.Tenant;
import io.mutex.user.exception.TenantNameExistException;
import io.mutex.user.service.TenantService;
import io.mutex.user.valueobject.ViewParamKey;

/**
 *
 * @author Florent
 */
@Named(value = "editTenantBacking")
@ViewScoped
public class EditTenantBacking extends QuantumEditBacking<Tenant> implements Serializable{
    
	private static final long serialVersionUID = 1L;

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
    public void edit() {
        switch(viewState){
            case CREATE:
                try {
                    tenantService.createTenant(currentTenant).ifPresent(this::returnToCaller);
                } catch (TenantNameExistException ex) {
                    addGlobalErrorMessage(ex.getMessage());
                }
                break;

            case UPDATE:
                try {
                     tenantService.updateTenant(currentTenant).ifPresent(this::returnToCaller);
                } catch (TenantNameExistException ex) {
                     addGlobalErrorMessage(ex.getMessage());
                }
                break;
        }

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
