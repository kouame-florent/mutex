/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.admin;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
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
import quantum.mutex.domain.entity.GroupType;
import quantum.mutex.domain.entity.StandardUser;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserGroup;
import quantum.mutex.domain.entity.UserStatus;
import quantum.mutex.domain.dao.StandardUserDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dao.UserRoleDAO;
import quantum.mutex.service.domain.UserRoleService;


/**
 *
 * @author Florent
 */
@Named(value = "userBacking")
@ViewScoped
public class UserBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(UserBacking.class.getName());
    
    @Inject StandardUserDAO standardUserDAO;
    @Inject UserDAO userDAO;
    @Inject UserRoleService userRoleService;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserRoleDAO userRoleDAO;
    
    private User selectedUser;
    
    private List<User> users = Collections.EMPTY_LIST;
    
    @PostConstruct
    public void init(){
        initUsers();
    }
    
    private void initUsers(){
        users = getTenantUsers();
    }
    
   
    private List<User> getTenantUsers(){
       return getUserTenant().map(standardUserDAO::findByTenant)
               .getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    public void openAddUserDialog(){
        
        Map<String,Object> options = getDialogOptions(45, 46,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_USER_DIALOG.id(), options, null);
    }
    
    public void openUpdateUserDialog(@NotNull User user){
        
        Map<String,Object> options = getDialogOptions(45, 46,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-user-dlg", options, 
                        getDialogParams(ViewParamKey.USER_UUID,
                                user.getUuid().toString()));
        LOG.log(Level.INFO, "-- USER UUID:{0}", user.getUuid().toString());
    }  
    
    public void openEditUserGroupDialog(@NotNull User user){
        
        Map<String,Object> options = getDialogOptions(55, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_USER_GROUP_DIALOG.id(), options, 
                        getDialogParams(ViewParamKey.USER_UUID,
                                user.getUuid().toString()));
    }  
    
    
    public void provideSelectedUser(@NotNull StandardUser standardUser){
        selectedUser = standardUser;
    }
    
    public String getUserMainGroup(@NotNull User user){
       return userGroupDAO.findUserPrimaryGroup(user)
                .map(ug -> ug.getGroup().getName()).getOrElse(() -> "");
      }
    
    public int getSecondaryGroupCount(@NotNull User user){
        return userGroupDAO.findByUserAndGroupType(user, GroupType.SECONDARY).size();
    }
    
    public List<String> getSecondaryGroupNames(@NotNull User user){
        return userGroupDAO.findByUserAndGroupType(user, GroupType.SECONDARY)
                .stream().map(ug -> ug.getGroup().getName())
                .collect(Collectors.toList());
    }
    
    public void delete(){  
       deleteUsersGroups.compose(deleteUserRoles)
               .apply(selectedUser).ifPresent(deleteUser);
   }
    
    private final Function<User,Optional<User>> deleteUsersGroups = (@NotNull User user) -> {
        Optional.ofNullable(user).map(u -> userGroupDAO.findByUser(u))
                .map(List::stream).orElseGet(() -> Stream.empty())
                .forEach(userGroupDAO::makeTransient);
        return Optional.of(user);
    };
    
    private final Function<User,User> deleteUserRoles = (@NotNull User user) -> {
                userRoleDAO.findByUser(user).stream()
                    .forEach(userRoleDAO::makeTransient);
                return user;
    };
    
    private final Consumer<User> deleteUser = (User user) -> {
         userDAO.makeTransient(user);
    };
    
//    private void deleteUsersGroups(User user){
//        Optional.ofNullable(user).map(u -> userGroupDAO.findByUser(u))
//                .map(List::stream).orElseGet(() -> Stream.empty())
//                .forEach(userGroupDAO::makeTransient);
//    }
    
    
    
//    private void deleteUser(@NotNull User user){
//       userDAO.makeTransient(user);
//    }
    
   
    
    
    
    public void handleAddUserReturn(SelectEvent event){
        LOG.log(Level.INFO, "--> HANDLE USER RET: {0}", event);
        initUsers();
        selectedUser = (User)event.getObject();
        userRoleService.cleanOrphanLogins();
    }
    
    public void enable(@NotNull User user){
        Optional.ofNullable(user).map(u -> this.setStatus.apply(u))
                .map(f -> f.apply(UserStatus.ENABLED))
                .ifPresent(userDAO::makePersistent);
        initUsers();
    }
    
    
    public void disable(@NotNull User user){
        Optional.ofNullable(user).map(u -> this.setStatus.apply(u))
                .map(f -> f.apply(UserStatus.DISABLED))
                .ifPresent(userDAO::makePersistent);
        initUsers();
    }
    
    Function<User,Function<UserStatus,User>> setStatus = user -> status -> {
        user.setStatus(status);
        return user;
    };
    
    public boolean showEnableLink(@NotNull User user){
        return (userGroupDAO.countAssociations(user) > 0) 
                && (user.getStatus().equals(UserStatus.DISABLED));
    }
    
    public boolean showDisableLink(@NotNull User user){
        return (userGroupDAO.countAssociations(user) > 0) 
                && (user.getStatus().equals(UserStatus.ENABLED));
    }
   
    
    public void handleDialogClose(CloseEvent closeEvent){
        initUsers();
    }
    
    

    public List<User> getUsers() {
        return users;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public UserGroupDAO getUserGroupDAO() {
        return userGroupDAO;
    }
    
    
}
