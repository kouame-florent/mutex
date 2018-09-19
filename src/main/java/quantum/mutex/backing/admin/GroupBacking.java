/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewID;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.domain.UserStatus;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.service.user.GroupService;


/**
 *
 * @author Florent
 */
@Named(value = "groupBacking")
@ViewScoped
public class GroupBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(GroupBacking.class.getName());
    
    @Inject private GroupDAO groupDAO;
    @Inject private GroupService groupService;
    @Inject private UserGroupDAO userGroupDAO;
    @Inject private UserDAO userDAO;
    
    private Group selectedGroup;
        
    private List<Group> groups = new ArrayList<>();
     
    @PostConstruct
    public void init(){
        initGroups();
    }
    
    private void initGroups(){
        groups = getUserTenant()
                .map(groupDAO::findByTenant).getOrElse(()-> Collections.EMPTY_LIST);
    }
    
    
    public void openAddGroupDialog(){
        LOG.log(Level.INFO, "OPEN  ADD GROUP DLG...");
        Map<String,Object> options = getDialogOptions(45, 40,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_GROUP_DIALOG.id(), options, null);
    }
    
    public void openUpdateGroupDialog(Group group){
        LOG.log(Level.INFO, "OPEN UPDATE GROUP: {0}",group.getName());
        Map<String,Object> options = getDialogOptions(45, 40,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_GROUP_DIALOG.id(), options, 
                        getDialogParams(ViewParamKey.GROUP_UUID,
                                group.getUuid().toString()));
    }
    
    public void provideSelectedGroup(@NotNull Group group){
        selectedGroup = group;
    }
    
    public void delete(){  
         disableUsers(selectedGroup);
         deleteUsersGroups(selectedGroup);
         deleteGroup(selectedGroup);
    }
    
    private List<User> retrieveInvolveUsers(Group group){
        return Optional.ofNullable(group).map(userGroupDAO::findByGroup)
                    .map(List::stream).orElseGet(() -> Stream.empty())
                    .map(UserGroup::getUser).collect(Collectors.toList());
    }
    
    private boolean withOnlyOneGroup(User user){
        return  userGroupDAO.countAssociations(user) == 1;
     }
    
    private User provideDisabled(User user){
        user.setStatus(UserStatus.DISABLED);
        return user;
    }
    
    private void disableUsers(Group group){
        retrieveInvolveUsers(group).stream().filter(this::withOnlyOneGroup)
                .map(this::provideDisabled).forEach(userDAO::makePersistent);
    }
    
    private void deleteGroup(Group group){
        Optional.ofNullable(group).ifPresent(groupDAO::makeTransient);
    }
    
    private void deleteUsersGroups(Group group){
        Optional.ofNullable(group).map(userGroupDAO::findByGroup)
                .map(List::stream).orElseGet(() -> Stream.empty())
                .forEach(userGroupDAO::makeTransient);
    }
    
    public void handleAddGroupReturn(SelectEvent event){
        initGroups();
        selectedGroup = (Group)event.getObject();
        
    }
    
    public void handleDialogClose(CloseEvent closeEvent){
        initGroups();
    }
    
    public long countGroupMembers(@NotNull Group group){
        return userGroupDAO.countGroupMembers(group);
    }
    
    public List<Group> getGroups() {
        return groups;
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

}
