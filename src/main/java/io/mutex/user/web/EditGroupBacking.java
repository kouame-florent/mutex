/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.index.valueobject.Constants;
import java.io.Serializable;
import java.util.Optional;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Space;
import io.mutex.user.exception.GroupNameExistException;
import io.mutex.user.service.GroupService;
import io.mutex.user.service.SpaceService;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.valueobject.ViewState;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Florent
 */
@Named(value = "editGroupBacking")
@ViewScoped
public class EditGroupBacking extends QuantumEditBacking<Group> implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(EditGroupBacking.class.getName());
    
    
    private final ContextIdParamKey groupParamKey = ContextIdParamKey.GROUP_UUID;
    
    @Inject 
    GroupService groupService; 
    
    @Inject
    private SpaceService spaceService;
    
    private Group currentGroup; 
    
    private List<Space> spaces = List.of();
    private Space selectedSpace;

    @Override
    public void viewAction(){
        currentGroup = initEntity(entityUUID);
        viewState = initViewState(entityUUID);
        selectedSpace = initSelectedSpace(viewState,currentGroup);
        spaces = spaceService.getAllSpaces();
    }
    
    @Override
    protected Group initEntity(String entityUUID) {
        return Optional.ofNullable(entityUUID)
                    .flatMap(groupService::findByUuid).orElseGet(() -> new Group());
    }
    
    private Space initSelectedSpace(ViewState viewState,Group group){
        if(viewState == ViewState.CREATE){
           return spaceService.getSpaceByName(Constants.DEFAULT_SPACE).orElseThrow();
        }else{
            return group.getSpace();
        }
    }

    @Override
    public void edit() {
        switch(viewState){
            case CREATE:
                {
                    try {
                        addSpaceToGroup();
                        groupService.create(currentGroup).ifPresent(this::returnToCaller);
                    } catch (GroupNameExistException ex) {
                        addGlobalErrorMessage(ex.getMessage());
                    }
                }
                break;
         
            case UPDATE:
                {
                    try {
                        addSpaceToGroup();
                        groupService.update(currentGroup).ifPresent(this::returnToCaller);
                    } catch (GroupNameExistException ex) {
                        addGlobalErrorMessage(ex.getMessage());
                    }

                 }
                break;
        }
    }
    
    private void addSpaceToGroup(){
        LOG.log(Level.INFO, "--> SELECTED SPACE: {0}", selectedSpace.getName());
        if(selectedSpace != null){
            currentGroup.setSpace(selectedSpace);
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

    public List<Space> getSpaces() {
        return spaces;
    }

    public Space getSelectedSpace() {
        return selectedSpace;
    }

    public void setSelectedSpace(Space selectedSpace) {
        this.selectedSpace = selectedSpace;
    }

    
}
