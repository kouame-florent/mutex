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
import java.util.function.Function;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.user.entity.Group;
import io.mutex.user.valueobject.GroupType;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.StandardUserDAO;
import io.mutex.user.repository.UserGroupDAO;
import io.mutex.user.service.GroupService;
import io.mutex.user.service.UserService;


/**
 *
 * @author Florent
 */
@Named(value = "editUserGroupBacking")
@ViewScoped
public class EditUserGroupBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(EditUserGroupBacking.class.getName());
     
    private final ViewParamKey userParamKey = ViewParamKey.USER_UUID;
    private String userUUID;
    
    @Inject StandardUserDAO standardUserDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserService userService;
    @Inject GroupService groupService;
    
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
                .flatMap(standardUserDAO::findById)
                .orElseGet(() -> new StandardUser());

    }
    
    public boolean rendererCheckSelectedButton( Group group){
        return group.isEdited();
    }
    
    public boolean rendererCheckPrimaryButton( Group group){
        return group.isPrimary();
    }
    
    public void uncheckSelected( Group group){
        group.setEdited(false);
        group.setPrimary(false);
    }
        
    public void checkSelected( Group group){  
        if(!hasPrimaryGroup(groups)){
            group.setPrimary(true);
        }
        group.setEdited(true);
    }

    private boolean hasPrimaryGroup( List<Group> groups){
         return groups.stream().anyMatch(Group::isPrimary);
    }
     
    public void persist(){
        createPrimaryUsersGroups(groups);
        createSecondaryUsersGroups(groups);
        removeUnselectedUsersGroups(groups);
        
        PrimeFaces.current().dialog().closeDynamic(currentUser);
    }
    
    private void createPrimaryUsersGroups(List<Group> groups){
        groups.stream().filter(Group::isEdited).filter(Group::isPrimary)
                    .map(g -> buildUserGroup.apply(currentUser).apply(g).apply(GroupType.PRIMARY))
                    .forEach(userGroupDAO::makePersistent);
    }
    
    private void createSecondaryUsersGroups(List<Group> groups){
        groups.stream().filter(Group::isEdited).filter(g -> !g.isPrimary())
                .map(g -> buildUserGroup.apply(currentUser).apply(g)
                        .apply(GroupType.SECONDARY))
                .forEach(userGroupDAO::makePersistent);
   }
    
    private void removeUnselectedUsersGroups(List<Group> groups){
        groups.stream().filter(g -> !g.isEdited())
            .map(g -> userGroupDAO.findByUserAndGroup(currentUser, g))
            .forEach(rug -> rug.map(userGroupDAO::makeTransient));
    }
      
    Function<User,Function<Group,Function<GroupType,UserGroup>>> buildUserGroup =
            user -> group -> type -> {return new UserGroup(user, group, type);};

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

    public ViewParamKey getUserParamKey() {
        return userParamKey;
    }

    public List<Group> getGroups() {
        return groups;
    }
}