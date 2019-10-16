/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.interfaces.web;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.component.effect.Effect;
import quantum.mutex.shared.interfaces.web.BaseBacking;
import quantum.mutex.shared.interfaces.web.ViewParamKey;
import quantum.mutex.shared.interfaces.web.ViewState;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.repository.GroupDAO;
import quantum.mutex.user.service.GroupService;


/**
 *
 * @author Florent
 */
@Named(value = "editGroupBacking")
@ViewScoped
public class EditGroupBacking extends BaseBacking implements Serializable{
    
    @Inject GroupDAO groupDAO;
    @Inject GroupService groupService;
    private Group currentGroup; 
    
    private final ViewParamKey groupParamKey = ViewParamKey.GROUP_UUID;
    private String groupUUID;
    private ViewState viewState = ViewState.CREATE;

    public void viewAction(){
        viewState = updateViewState(groupUUID);
        currentGroup = retriveGroup(groupUUID);
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
    
    
    private ViewState updateViewState(String groupUUID){
        return StringUtils.isBlank(groupUUID) ? ViewState.CREATE
                : ViewState.UPDATE;
    }
    
    private void returnToCaller(Group group){
        PrimeFaces.current().dialog().closeDynamic(group);
    } 
    

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
