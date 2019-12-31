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
import org.primefaces.PrimeFaces;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Tenant;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.service.GroupService;
import io.mutex.user.valueobject.ViewParamKey;
import io.mutex.user.valueobject.ViewState;

import org.apache.commons.lang3.StringUtils;


/**
 *
 * @author Florent
 */
@Named(value = "editGroupBacking")
@ViewScoped
public class EditGroupBacking extends QuantumEditBacking<Group> implements Serializable{
    
    private static final long serialVersionUID = 1L;
	
    @Inject GroupDAO groupDAO;
    @Inject GroupService groupService;
    private Group currentGroup; 
    
    private final ViewParamKey groupParamKey = ViewParamKey.GROUP_UUID;
    private String groupUUID;
    private ViewState viewState = ViewState.CREATE;

    @Override
    public void viewAction(){
        currentGroup = retriveGroup(groupUUID);
        viewState = initViewState(groupUUID);
        
    }
    
    @Override
    protected Group initEntity(String entityUUID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void edit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    private Group retriveGroup(String groupUUID){
       return Optional.ofNullable(groupUUID)
                    .flatMap(groupDAO::findById).orElseGet(() -> new Group());
   } 
    
    public void persist(){  
        getUserTenant().map(t -> provideTenant.apply(t).apply(currentGroup))
                .flatMap(groupService::initGroup)
                .ifPresent(this::returnToCaller);
    }
     
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

    public String getGroupUUID() {
        return groupUUID;
    }

    public void setGroupUUID(String groupUUID) {
        this.groupUUID = groupUUID;
    }

    public ViewState getViewState() {
        return viewState;
    }

   
}
