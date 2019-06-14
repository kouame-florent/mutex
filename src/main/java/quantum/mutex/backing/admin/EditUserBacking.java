/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.admin;


import java.io.Serializable;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
import quantum.mutex.domain.entity.Role;
import quantum.mutex.domain.entity.RoleName;
import quantum.mutex.domain.entity.StandardUser;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserRole;
import quantum.mutex.domain.entity.UserStatus;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.RoleDAO;
import quantum.mutex.domain.dao.StandardUserDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserRoleDAO;
import quantum.mutex.service.domain.UserRoleService;
import quantum.mutex.service.EncryptionService;
import quantum.mutex.service.domain.UserService;


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
    
    private final Function<String, StandardUser> retrieveUser = uuidStr -> Result.of(uuidStr)
                .flatMap(standardUserDAO::findById)
                .getOrElse(() -> new StandardUser());
 
    
    public void persist(){
        LOG.log(Level.INFO, "---> PESIST USER {0}", currentUser);
        
        Result<StandardUser> res = Result.of(currentUser)
                .flatMap(cu -> validatePassword.apply(cu));
        
        res.forEachOrFail(u -> {}).forEach(showInvalidPasswordMessage);
        Result<StandardUser> user = res.flatMap(u -> persisteUser.apply(u));
        user.map(u -> userRoleService.persistUserRole(u, RoleName.USER));
        
        user.forEach(u -> returnToCaller.apply((StandardUser)u));
    }
    
    private final Function<StandardUser,Result<StandardUser>> validatePassword = user ->{
        return user.getPassword().equals(user.getConfirmPassword()) ? 
                  Result.success(user) : Result.failure(new Exception("user.password.validation.error")) ;
    };

    private Function<StandardUser,Result<StandardUser>> persisteUser = user ->{
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
    
//    private Function<StandardUser,Result<UserRole>> persistUserRole = user ->{
//        
//        Result<User> rootUser = userDAO.findByLogin(user.getLogin());
//        Result<Role> rootRole = roleDAO.findByName(RoleName.USER);
//
//        Result<UserRole> usr = rootUser
//                .flatMap(ru -> rootRole.flatMap(rr -> userRoleDAO.findByUserAndRole(ru.getLogin(),rr.getName())));
//        
//        if(usr.isEmpty()){
//            return this.findRole.apply(RoleName.USER).map(r -> this.createUserRole.apply(r))
//                    .map(f -> f.apply(user)).flatMap(userRoleDAO::makePersistent)
//                    .orElse(() -> Result.empty());
//        }
//        
//        return Result.empty();
//     };
    
  
    private final Function<RoleName,Result<Role>> findRole = roleName -> {
        return roleDAO.findByName(roleName);
    };
      
    private final Function<Role,Function<User,UserRole>> createUserRole = 
            role -> user ->{
        return new UserRole(user, role);
    };
   
   
    private final Effect<String> showInvalidPasswordMessage = key ->{
         addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
    };
 
    private final Effect<User> returnToCaller = (user) ->
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
