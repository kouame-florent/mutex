/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;


import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.user.entity.Role;
import io.mutex.search.valueobject.RoleName;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.entity.Tenant;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;
import io.mutex.search.valueobject.UserStatus;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.RoleDAO;
import io.mutex.user.repository.StandardUserDAO;
import io.mutex.user.repository.UserDAO;
import io.mutex.user.repository.UserRoleDAO;
import io.mutex.user.service.UserRoleService;
import io.mutex.shared.service.EncryptionService;
import io.mutex.user.service.UserService;
import org.apache.commons.lang3.StringUtils;


/**
 *
 * @author Florent
 */
@Named(value = "editUserBacking")
@ViewScoped
public class EditUserBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(EditUserBacking.class.getName());
   
    
    private final ViewParamKey userParamKey = ViewParamKey.USER_UUID;
    private String userUUID;  
    private ViewState viewState;
    
    @Inject StandardUserDAO standardUserDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserRoleDAO userRoleDAO;
    @Inject UserDAO userDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserService userService;
    @Inject UserRoleService userRoleService;
//    @Inject EncryptionService encryptionService;
 
    private StandardUser currentUser;
     
    public void viewAction(){
        viewState = updateViewState(userUUID);
//        Function<String, StandardUser> initUser = presetConfirmPassword.compose(retrieveUser);
//        currentUser = initUser.apply(userUUID);
          currentUser = retrieveUser(userUUID);
          currentUser = presetConfirmPassword(currentUser);
        
    }
    
    private StandardUser retrieveUser(String userUUID){
        return Optional.ofNullable(userUUID)
                .flatMap(standardUserDAO::findById)
                .orElseGet(() -> new StandardUser());

    }
    
    private StandardUser presetConfirmPassword(StandardUser standardUser){
       standardUser.setConfirmPassword(standardUser.getPassword());
       return standardUser;
    }
    
   
     
//    private final Function<StandardUser, StandardUser> presetConfirmPassword = user -> {
//        user.setConfirmPassword(user.getPassword());
//        return user;
//    };
//    
//    private final Function<String, StandardUser> retrieveUser = uuidStr -> Optional.of(uuidStr)
//                .flatMap(standardUserDAO::findById)
//                .orElseGet(() -> new StandardUser());
// 
    private ViewState updateViewState(String groupUUID){
        return StringUtils.isBlank(groupUUID) ? ViewState.CREATE
                : ViewState.UPDATE;
    }
    
    public void persist(){
        LOG.log(Level.INFO, "---> PESIST USER {0}", currentUser);
        
        Optional<StandardUser> res = Optional.of(currentUser)
                .flatMap(cu -> validatePassword.apply(cu));
        
        res.ifPresentOrElse(u -> {},this::showInvalidPasswordMessage);
        Optional<StandardUser> user = res.flatMap(u -> persisteUser.apply(u));
        user.map(u -> userRoleService.create(u, RoleName.USER));
        
        user.ifPresent(u -> returnToCaller.accept((StandardUser)u));  
    }
    
    private final Function<StandardUser,Optional<StandardUser>> validatePassword = user ->{
        return user.getPassword().equals(user.getConfirmPassword()) ? 
                  Optional.ofNullable(user) : Optional.empty() ;
    };

    private Function<StandardUser,Optional<StandardUser>> persisteUser = user ->{
        return getUserTenant().map(t -> this.provideTenant.apply(t).apply(user))
                    .map(u -> this.provideStatus.apply(u).apply(UserStatus.DISABLED))
                    .map(u -> this.provideHashedPassword.apply(u))
                    .flatMap(standardUserDAO::makePersistent);
    };
    
    private final Function<Tenant,Function<StandardUser,StandardUser>> provideTenant = (tenant) ->
            user -> {user.setTenant(tenant); return user;};
    
      
    private final Function<StandardUser,Function<UserStatus,StandardUser>> provideStatus = (user) ->
            status -> {user.setStatus(status); return user;};
  
    
    private final Function<StandardUser,StandardUser> provideHashedPassword =  (user) -> {
        user.setPassword(EncryptionService.hash(user.getPassword()));
        return user;
    };
    

  
    private final Function<RoleName,Optional<Role>> findRole = roleName -> {
        return roleDAO.findByName(roleName);
    };
      
    private final Function<Role,Function<User,UserRole>> createUserRole = 
            role -> user ->{
        return new UserRole(user, role);
    };
   
    
    private void showInvalidPasswordMessage(){
        addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
    }
   
//    private final Consumer<String> showInvalidPasswordMessage = key ->{
//         addMessageFromResourceBundle(null, "user.password.validation.error", 
//                FacesMessage.SEVERITY_ERROR);
//    };
// 
    private final Consumer<User> returnToCaller = (user) ->
            PrimeFaces.current().dialog().closeDynamic(user);
    
    public StandardUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(StandardUser currentUser) {
        this.currentUser = currentUser;
    }

    public ViewParamKey getUserParamKey() {
        return userParamKey;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public ViewState getViewState() {
        return viewState;
    }

}
