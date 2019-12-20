/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

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
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import io.mutex.user.valueobject.GroupType;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.entity.User;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.repository.StandardUserDAO;
import io.mutex.user.repository.UserDAO;
import io.mutex.user.repository.UserGroupDAO;
import io.mutex.user.repository.UserRoleDAO;
import io.mutex.user.service.UserRoleService;


/**
 *
 * @author Florent
 */
@Named(value = "userBacking")
@ViewScoped
public class UserBacking extends QuantumBacking<User> implements Serializable{

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
        users = initView(this::getTenantUsers);
    }
    
//    Supplier<List<User>> getTenantUsers = () -> {
//        return getUserTenant().map(standardUserDAO::findByTenant)
//               .orElseGet(() -> Collections.EMPTY_LIST);
//    };
   
    private List<User> getTenantUsers(){
       return getUserTenant().map(standardUserDAO::findByTenant)
               .orElseGet(() -> Collections.EMPTY_LIST);
    }
//    
//     @Override
//    protected Map<String, Object> provideDialogOptions() {
//        return getDialogOptions(65, 66,true);
//    }

//    @Override
//    protected void openDialog(Map<String, Object> options,String viewID) {
//       PrimeFaces.current().dialog()
//                .openDynamic(viewID, options, null);
//    }
    
    @Override
    protected String viewId() {
        return ViewID.EDIT_USER_DIALOG.id();
    }
        
    @Override
    public void delete() {
        deleteUsersGroups.compose(deleteUserRoles)
               .apply(selectedUser).ifPresent(deleteUser);
    }
    
    public void openAddUserDialog(){
        
        Map<String,Object> options = getDialogOptions(65, 66,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_USER_DIALOG.id(), options, null);
    }
    
    public void openUpdateUserDialog( User user){
        
        Map<String,Object> options = getDialogOptions(65, 56,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-user-dlg", options, 
                        getDialogParams(ViewParamKey.USER_UUID,
                                user.getUuid()));
        LOG.log(Level.INFO, "-- USER UUID:{0}", user.getUuid());
    }  
    
    public void openEditUserGroupDialog( User user){
      
        Map<String,Object> options = getDialogOptions(55, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_USER_GROUP_DIALOG.id(), options, 
                        getDialogParams(ViewParamKey.USER_UUID,
                                user.getUuid()));
    }  
    
    
    public void provideSelectedUser( StandardUser standardUser){
        selectedUser = standardUser;
    }
    
    public String getUserMainGroup( User user){
       return userGroupDAO.findUserPrimaryGroup(user)
                .map(ug -> ug.getGroup().getName()).orElseGet(() -> "");
      }
    
    public int getSecondaryGroupCount( User user){
        return userGroupDAO.findByUserAndGroupType(user, GroupType.SECONDARY).size();
    }
    
    public List<String> getSecondaryGroupNames( User user){
        return userGroupDAO.findByUserAndGroupType(user, GroupType.SECONDARY)
                .stream().map(ug -> ug.getGroup().getName())
                .collect(Collectors.toList());
    }
    
//    public void delete(){  
//       deleteUsersGroups.compose(deleteUserRoles)
//               .apply(selectedUser).ifPresent(deleteUser);
//   }
    
    private final Function<User,Optional<User>> deleteUsersGroups = (User user) -> {
        Optional.ofNullable(user).map(u -> userGroupDAO.findByUser(u))
                .map(List::stream).orElseGet(() -> Stream.empty())
                .forEach(userGroupDAO::makeTransient);
        return Optional.of(user);
    };
    
    private final Function<User,User> deleteUserRoles = ( User user) -> {
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
    
    
    
//    private void deleteUser( User user){
//       userDAO.makeTransient(user);
//    }
    
   
    
    
    
    public void handleAddUserReturn(SelectEvent event){
        LOG.log(Level.INFO, "--> HANDLE USER RET: {0}", event);
        initUsers();
        selectedUser = (User)event.getObject();
        userRoleService.cleanOrphanLogins();
    }
    
    public void enable( User user){
        Optional.ofNullable(user).map(u -> this.setStatus.apply(u))
                .map(f -> f.apply(UserStatus.ENABLED))
                .ifPresent(userDAO::makePersistent);
        initUsers();
    }
    
    
    public void disable( User user){
        Optional.ofNullable(user).map(u -> this.setStatus.apply(u))
                .map(f -> f.apply(UserStatus.DISABLED))
                .ifPresent(userDAO::makePersistent);
        initUsers();
    }
    
    Function<User,Function<UserStatus,User>> setStatus = user -> status -> {
        user.setStatus(status);
        return user;
    };
    
    public boolean showEnableLink( User user){
        return (userGroupDAO.countAssociations(user) > 0) 
                && (user.getStatus().equals(UserStatus.DISABLED));
    }
    
    public boolean showDisableLink( User user){
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
