/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Tenant;
import io.mutex.user.exception.GroupNameExistException;
import io.mutex.user.service.GroupService;
import io.mutex.user.valueobject.ViewParamKey;
import io.mutex.user.valueobject.ViewState;
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
	
//    @Inject GroupDAO groupDAO;
    @Inject GroupService groupService;
    private Group currentGroup; 
    
    private final ViewParamKey groupParamKey = ViewParamKey.GROUP_UUID;
//    private String groupUUID;
//    private ViewState viewState = ViewState.CREATE;

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

                
        }
    }

    
//    private Group retriveGroup(String groupUUID){
//       return Optional.ofNullable(groupUUID)
//                    .flatMap(groupDAO::findById).orElseGet(() -> new Group());
//   } 
    
//    public void persist(){  
//        getUserTenant().map(t -> provideTenant.apply(t).apply(currentGroup))
//                .flatMap(groupService::initGroup)
//                .ifPresent(this::returnToCaller);
//    }
    
    
     
    private final Function<Tenant, Function<Group, Group>> provideTenant = 
            (tenant) ->  group -> {group.setTenant(tenant); return group;};
    
    
//    private ViewState updateViewState(String groupUUID){
//        return StringUtils.isBlank(groupUUID) ? ViewState.CREATE
//                : ViewState.UPDATE;
//    }
    
//    private void returnToCaller(Group group){
//        PrimeFaces.current().dialog().closeDynamic(group);
//    } 
    

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }

    public ViewParamKey getGroupParamKey() {
        return groupParamKey;
    }

//    public String getGroupUUID() {
//        return groupUUID;
//    }
//
//    public void setGroupUUID(String groupUUID) {
//        this.groupUUID = groupUUID;
//    }

    public ViewState getViewState() {
        return viewState;
    }

   
}
