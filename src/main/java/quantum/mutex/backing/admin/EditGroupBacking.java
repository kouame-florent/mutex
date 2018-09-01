/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.admin;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.dao.GroupDAO;

/**
 *
 * @author Florent
 */
@Named(value = "editGroupBacking")
@ViewScoped
public class EditGroupBacking extends BaseBacking implements Serializable{
    
    @Inject GroupDAO groupDAO;
   
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
                .map(UUID::fromString).flatMap(groupDAO::findById)
                .orElseGet(() -> new Group());
    } 
    
    public void persist(){  
        getUserTenant().map(provideGroup)
                .map(f -> f.apply(currentGroup))
                .flatMap(groupDAO::makePersistent).ifPresent(returnToCaller);
    }
     
    private final Function<Tenant, Function<Group, Group>> provideGroup = 
            (tenant) ->  group -> {group.setTenant(tenant); return group;};
    
    
    private ViewState updateViewState(String groupUUID){
        return StringUtils.isBlank(groupUUID) ? ViewState.CREATE
                : ViewState.UPDATE;
    }
    
    private final Consumer<Group> returnToCaller = (group) ->
            PrimeFaces.current().dialog().closeDynamic(group);
    

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
