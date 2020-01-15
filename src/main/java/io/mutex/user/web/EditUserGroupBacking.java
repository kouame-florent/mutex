/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.user.entity.Group;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.exception.NoPrimaryGroupException;
import io.mutex.user.service.GroupService;
import io.mutex.user.service.StandardUserService;
import io.mutex.user.service.UserGroupService;



/**
 *
 * @author Florent
 */
@Named(value = "editUserGroupBacking")
@ViewScoped
public class EditUserGroupBacking extends BaseBacking implements Serializable{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(EditUserGroupBacking.class.getName());
     
    private final ContextIdParamKey userParamKey = ContextIdParamKey.USER_UUID;
    private String userUUID;
    
    @Inject StandardUserService standardUserService;
    @Inject GroupService groupService;
    @Inject UserGroupService userGroupService;
    
    private StandardUser currentUser;
    private Group selectedGroup;
    private List<Group> groups = new ArrayList<>();
    
    public void viewAction(){
        currentUser = initCurrentUser(userUUID);
        groups = initUserGroups(currentUser);
    }
    
    private List<Group> initUserGroups(StandardUser user){
        return groupService.initUserGroups(user);
    }
    
    private StandardUser initCurrentUser(String userUUID){
        return Optional.ofNullable(userUUID)
                .flatMap(standardUserService::findByUuid)
                .orElseGet(() -> new StandardUser());

    }
    
    public boolean rendererCheckSelectedButton( Group group){
        return group.isEdited();
    }
    
    public boolean rendererCheckPrimaryButton( Group group){
        return group.isPrimary();
    }
    
    public void uncheckSelected(Group group){
        if(group.isPrimary()){
            addGlobalWarningMessage("Vous devez indiquer un groupe principal!");
        }
        group.setEdited(false);
        group.setPrimary(false);
    }
        
    public void checkSelected(Group group){  
        if(!hasPrimaryGroup(groups)){
            setAsPrimary(group);
        }
        group.setEdited(true);
    }
    
    public boolean rendererSetAsPrincipalButton(Group group){
        return isChecked(group) && !hasPrimaryGroup() ;
    }
    
    public boolean isChecked(Group group){
        return group.isEdited();
    }
    
    public boolean hasPrimaryGroup(){
       return hasPrimaryGroup(groups);
    }

    private boolean hasPrimaryGroup( List<Group> groups){
         return groups.stream().anyMatch(Group::isPrimary);
    }
         
    public void setAsPrimary(Group group){
       if(!hasPrimaryGroup(groups)){
           group.setPrimary(true);
       }
    }
    
    public void associateGroups(){
        try {
            associateGroups_();
            PrimeFaces.current().dialog().closeDynamic(currentUser);
        } catch (NoPrimaryGroupException ex) {
            addGlobalErrorMessage(ex.getMessage());
        }
    }
    
    private void associateGroups_() throws NoPrimaryGroupException{
         if(!hasPrimaryGroup(groups)){
            throw new NoPrimaryGroupException("Il n'exist aucun groupe principal.");
        }
        userGroupService.associateGroups(groups, currentUser);
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public StandardUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(StandardUser currentUser) {
        this.currentUser = currentUser;
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public ContextIdParamKey getUserParamKey() {
        return userParamKey;
    }

    public List<Group> getGroups() {
        return groups;
    }
}
