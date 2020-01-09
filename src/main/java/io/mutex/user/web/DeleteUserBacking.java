/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.user.entity.StandardUser;
import io.mutex.user.service.StandardUserService;
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
@Named(value = "deleteUserBacking")
@ViewScoped
public class DeleteUserBacking extends QuantumDeleteBacking<StandardUser> implements Serializable{
     
    private static final long serialVersionUID = 1L;
   
    private final ContextIdParamKey userParamKey = ContextIdParamKey.USER_UUID;
    
    @Inject StandardUserService standardUserService;
      
    @Override
    public void delete(){
        Optional.ofNullable(entityUUID)
                .flatMap(standardUserService::findByUuid)
                .ifPresent(standardUserService::delete);
        closeDeleteView();
    }

    public ContextIdParamKey getUserParamKey() {
        return userParamKey;
    }
   
}
