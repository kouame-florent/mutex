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
import io.mutex.user.exception.SpaceNameExistException;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.service.SpaceService;

/**
 *
 * @author Florent
 */
@Named(value = "editSpaceBacking")
@ViewScoped
public class EditSpaceBacking extends QuantumEditBacking<Space> implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private final ContextIdParamKey spaceParamKey = ContextIdParamKey.SPACE_UUID;
    
    @Inject
    private SpaceService spaceService; 
    private Space currentSpace;
    
    @Override
    public void viewAction(){
         currentSpace = initEntity(entityUUID);
         viewState = initViewState(entityUUID);
    }
    
    @Override
    protected Space initEntity(String entityUUID) {
        return Optional.ofNullable(entityUUID)
                .flatMap(spaceService::findByUuid)
                .orElseGet(() -> new Space());
    }

    @Override
    public void edit() {
        switch(viewState){
            case CREATE:
                try {
                    spaceService.create(currentSpace).ifPresent(this::returnToCaller);
                } catch (SpaceNameExistException ex) {
                    addGlobalErrorMessage(ex.getMessage());
                }
                break;

            case UPDATE:
                try {
                     spaceService.update(currentSpace).ifPresent(this::returnToCaller);
                } catch (SpaceNameExistException ex) {
                     addGlobalErrorMessage(ex.getMessage());
                }
                break;
        }

    }

    public ContextIdParamKey getSpaceParamKey() {
        return spaceParamKey;
    }

    public Space getCurrentSpace() {
        return currentSpace;
    }

    public void setCurrentSpace(Space currentSpace) {
        this.currentSpace = currentSpace;
    }

}
