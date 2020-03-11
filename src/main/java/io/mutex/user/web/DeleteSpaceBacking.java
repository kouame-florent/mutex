/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.user.entity.Space;
import io.mutex.user.valueobject.ContextIdParamKey;
import java.io.Serializable;
import java.util.Optional;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.service.SpaceService;

/**
 *
 * @author root
 */
@Named(value = "deleteSpaceBacking")
@ViewScoped
public class DeleteSpaceBacking extends QuantumDeleteBacking<Space> implements Serializable{
    
    @Inject SpaceService spaceService;     
    
    @Override
    protected void postConstruct() {
        iniCtxtParamKey(ContextIdParamKey.SPACE_UUID);
    }
    
    @Override
    public void delete() {
        Optional.ofNullable(entityUUID)
                .flatMap(spaceService::findByUuid)
                .ifPresent(spaceService::delete);
        closeDeleteView();
    }

}
