/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.admin;

import java.io.Serializable;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.dao.GroupDAO;

/**
 *
 * @author Florent
 */
@Named(value = "editGroupBacking")
@RequestScoped
public class EditGroupBacking extends BaseBacking implements Serializable{
    
    @Inject GroupDAO groupDAO;
   
    private Group currentGroup; 
    
    private final ViewParamKey groupParamKey = ViewParamKey.GROUP_UUID;
    private String groupUUID;
    private ViewState viewState = ViewState.CREATE;
    
    @PostConstruct
    public void init(){
        currentGroup = new Group();
    }
    
    public void viewAction(){
        if(groupUUID != null){
            viewState = ViewState.UPDATE;
            currentGroup = groupDAO.findById(UUID.fromString(groupUUID));
        }
    }
    
    public void persist(){  
        //if(getUserTenant().isPresent() && currentGroup.getTenant() == null){
        if(viewState == ViewState.CREATE ){
            currentGroup.setTenant(getUserTenant().get());
        }
        Group persistentGroup = groupDAO.makePersistent(currentGroup);
        PrimeFaces.current().dialog().closeDynamic(persistentGroup);
    }
    
    public void close(){
        PrimeFaces.current().dialog().closeDynamic(null);
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

   
    
    
}
