/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.user.entity.Admin;
import io.mutex.user.valueobject.ContextIdParamKey;
import java.io.Serializable;
import java.util.Optional;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.service.AdminService;

/**
 *
 * @author root
 */
@Named(value = "deleteAdminBacking")
@ViewScoped
public class DeleteAdminBacking extends QuantumDeleteBacking<Admin> implements Serializable{

    @Inject AdminService adminService;
    
    @Override
    protected void postConstruct() {
        iniCtxtParamKey(ContextIdParamKey.ADMIN_UUID);
    }

    @Override
    public void delete() {
        Optional.ofNullable(entityUUID)
                .flatMap(adminService::findByUuid)
                .ifPresent(adminService::delete);
        closeDeleteView();
    }
    
}
