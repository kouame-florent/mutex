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
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
import static quantum.mutex.common.IfElse.*;
import quantum.mutex.domain.Role;
import quantum.mutex.domain.RoleName;
import quantum.mutex.domain.StandardUser;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserRole;
import quantum.mutex.domain.UserStatus;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.RoleDAO;
import quantum.mutex.domain.dao.StandardUserDAO;
import quantum.mutex.domain.dao.UserRoleDAO;
import quantum.mutex.service.EncryptionService;
import quantum.mutex.service.user.UserService;


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
    @Inject RoleDAO roleDAO;
    @Inject UserService userService;
    @Inject EncryptionService encryptionService;
 
    private StandardUser currentUser;
     
    public void viewAction(){
        viewState = updateViewState(userUUID);
        Function<String, StandardUser> initUser = presetConfirmPassword.compose(retrieveUser);
        currentUser = initUser.apply(userUUID);
        
    }
    
     private ViewState updateViewState(String groupUUID){
        return StringUtils.isBlank(groupUUID) ? ViewState.CREATE
                : ViewState.UPDATE;
    }
     
    private final Function<StandardUser, StandardUser> presetConfirmPassword = user -> {
        user.setConfirmPassword(user.getPassword());
        return user;
    };
    
    Function<String, StandardUser> retrieveUser = uuidStr -> Optional.ofNullable(uuidStr)
                .map(UUID::fromString).flatMap(standardUserDAO::findById)
                .orElseGet(() -> new StandardUser());
 
    
    public void persist(){
        ifElse(isPasswordValid(currentUser),() -> persistUser, 
                    () -> showInvalidPasswordMsg).accept(currentUser);
    }
    
    Consumer<StandardUser> showInvalidPasswordMsg = user -> {
        Optional.ofNullable(user).ifPresent(x -> showInvalidPasswordMessage());
    };
    
    Consumer<StandardUser> persistUser = user -> {
        getUserTenant().map(this.provideTenant)
                .map(f -> f.apply(user))
                .map(this.provideStatus).map(f -> f.apply(UserStatus.DISABLED))
                .map(this.providePassword).flatMap(standardUserDAO::makePersistent)
                .flatMap(this.persistUserRole).map(UserRole::getUser)
                .ifPresent(this.returnToCaller);
    };
    
    private final Function<Tenant,Function<StandardUser,StandardUser>> provideTenant = (tenant) ->
            user -> {user.setTenant(tenant); return user;};
    
      
    private final Function<StandardUser,Function<UserStatus,StandardUser>> provideStatus = (user) ->
            status -> {user.setStatus(status); return user;};
  
    
    private final Function<StandardUser,StandardUser> providePassword =  (user) -> {
        user.setPassword(encryptionService.hash(user.getPassword()));
        return user;
    };
    
    private Function<StandardUser,Optional<UserRole>> persistUserRole = user ->{
        return this.findRole.apply(RoleName.USER).map(this.createUserRole)
                    .map(f -> f.apply(user)).map(userRoleDAO::makePersistent)
                    .orElseGet(() -> Optional.empty());
    };
            
    
    private final Function<RoleName,Optional<Role>> findRole = roleName -> {
        return roleDAO.findByName(roleName);
    };
    
    private final Function<Role,Function<User,UserRole>> createUserRole = 
            role -> user ->{
        return new UserRole(user, role);
    };
     
    private boolean isPasswordValid(@NotNull User user){
        return user.getPassword().equals(user.getConfirmPassword());
    }
    
    
    
    private void showInvalidPasswordMessage(){
        addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
    }
      
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
