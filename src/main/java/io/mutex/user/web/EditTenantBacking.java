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
import io.mutex.user.entity.Space;
import io.mutex.user.exception.TenantNameExistException;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.service.SpaceService;

/**
 *
 * @author Florent
 */
@Named(value = "editTenantBacking")
@ViewScoped
public class EditTenantBacking extends QuantumEditBacking<Space> implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private final ContextIdParamKey tenantParamKey = ContextIdParamKey.TENANT_UUID;
    
    @Inject
    private SpaceService tenantService;
    private Space currentTenant;
    
    @Override
    public void viewAction(){
         currentTenant = initEntity(entityUUID);
         viewState = initViewState(entityUUID);
    }
    
    @Override
    protected Space initEntity(String entityUUID) {
        return Optional.ofNullable(entityUUID)
                .flatMap(tenantService::findByUuid)
                .orElseGet(() -> new Space());
    }

    @Override
    public void edit() {
        switch(viewState){
            case CREATE:
                try {
                    tenantService.create(currentTenant).ifPresent(this::returnToCaller);
                } catch (TenantNameExistException ex) {
                    addGlobalErrorMessage(ex.getMessage());
                }
                break;

            case UPDATE:
                try {
                     tenantService.update(currentTenant).ifPresent(this::returnToCaller);
                } catch (TenantNameExistException ex) {
                     addGlobalErrorMessage(ex.getMessage());
                }
                break;
        }

    }

    public ContextIdParamKey getTenantParamKey() {
        return tenantParamKey;
    }

    public Space getCurrentTenant() {
        return currentTenant;
    }

    public void setCurrentTenant(Space currentTenant) {
        this.currentTenant = currentTenant;
    }

}
