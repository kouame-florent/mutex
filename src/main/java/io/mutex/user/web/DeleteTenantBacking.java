/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.user.entity.Tenant;
import io.mutex.user.service.TenantService;
import io.mutex.user.valueobject.ContextIdParamKey;
import java.io.Serializable;
import java.util.Optional;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author root
 */
@Named(value = "deleteTenantBacking")
@ViewScoped
public class DeleteTenantBacking extends QuantumDeleteBacking<Tenant> implements Serializable{
    
    @Inject TenantService tenantService;    
    
    @Override
    protected void postConstruct() {
        iniCtxtParamKey(ContextIdParamKey.TENANT_UUID);
    }
    
    @Override
    public void delete() {
        Optional.ofNullable(entityUUID)
                .flatMap(tenantService::findByUuid)
                .ifPresent(tenantService::delete);
        closeDeleteView();
    }

}
