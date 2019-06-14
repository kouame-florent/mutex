/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import org.primefaces.PrimeFaces;
import quantum.functional.api.Result;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.GroupType;
import quantum.mutex.domain.entity.StandardUser;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserGroup;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.StandardUserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.service.domain.GroupService;
import quantum.mutex.service.domain.UserService;

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
    
    private List<Group> initUserGroups(@NotNull StandardUser user){
        return groupService.initUserGroups(user);
    }
    
    private StandardUser initCurrentUser(@NotNull String userUUID){
        return Result.of(userUUID)
                .flatMap(standardUserDAO::findById)
                .getOrElse(() -> new StandardUser());

    }
    
    public boolean rendererCheckSelectedButton(@NotNull Group group){
        return group.isEdited();
    }
    
    public boolean rendererCheckPrimaryButton(@NotNull Group group){
        return group.isPrimary();
    }
    
    public void uncheckSelected(@NotNull Group group){
        group.setEdited(false);
        group.setPrimary(false);
    }
        
    public void checkSelected(@NotNull Group group){  
        if(!hasPrimaryGroup(groups)){
            group.setPrimary(true);
        }
        group.setEdited(true);
    }

    private boolean hasPrimaryGroup(@NotNull List<Group> groups){
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
