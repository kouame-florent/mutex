/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.user.entity.AdminUser;
import io.mutex.user.service.AdminUserService;
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
@Named(value = "deleteAdminUserBacking")
@ViewScoped
public class DeleteAdminUserBacking extends QuantumDeleteBacking<AdminUser> implements Serializable{

    @Inject AdminUserService adminUserService;
    
    @Override
    protected void postConstruct() {
        iniCtxtParamKey(ContextIdParamKey.ADMIN_UUID);
    }

    @Override
    public void delete() {
        Optional.ofNullable(entityUUID)
                .flatMap(adminUserService::findByUuid)
                .ifPresent(adminUserService::delete);
        closeDeleteView();
    }
    
}
