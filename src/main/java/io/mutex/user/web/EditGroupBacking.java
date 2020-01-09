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
import io.mutex.user.entity.Group;
import io.mutex.user.exception.GroupNameExistException;
import io.mutex.user.service.GroupService;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.valueobject.ViewState;



/**
 *
 * @author Florent
 */
@Named(value = "editGroupBacking")
@ViewScoped
public class EditGroupBacking extends QuantumEditBacking<Group> implements Serializable{
    
    private static final long serialVersionUID = 1L;
	
    @Inject GroupService groupService;
    private Group currentGroup; 
    
    private final ContextIdParamKey groupParamKey = ContextIdParamKey.GROUP_UUID;

    @Override
    public void viewAction(){
        currentGroup = initEntity(entityUUID);
        viewState = initViewState(entityUUID);
    }
    
    @Override
    protected Group initEntity(String entityUUID) {
        return Optional.ofNullable(entityUUID)
                    .flatMap(groupService::findByUuid).orElseGet(() -> new Group());
    }

    @Override
    public void edit() {
        switch(viewState){
            case CREATE:
                {
                    try {
                        groupService.createGroup(currentGroup).ifPresent(this::returnToCaller);
                    } catch (GroupNameExistException ex) {
                        addGlobalErrorMessage(ex.getMessage());
                    }
                }
                break;
         
            case UPDATE:
                {
                    try {
                        groupService.updateGroup(currentGroup).ifPresent(this::returnToCaller);
                    } catch (GroupNameExistException ex) {
                        addGlobalErrorMessage(ex.getMessage());
                    }

                 }
                break;
        }
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }

    public ContextIdParamKey getGroupParamKey() {
        return groupParamKey;
    }

    public ViewState getViewState() {
        return viewState;
    }

}
