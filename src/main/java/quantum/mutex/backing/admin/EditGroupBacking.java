/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.admin;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.component.effect.Effect;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.service.domain.GroupService;


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
       return Optional.of(groupUUID)
                    .flatMap(groupDAO::findById).orElseGet(() -> new Group());
   } 
    
    public void persist(){  
        getUserTenant().map(t -> provideTenant.apply(t).apply(currentGroup))
                .flatMap(groupService::initGroup)
                .forEach(this::returnToCaller);
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
