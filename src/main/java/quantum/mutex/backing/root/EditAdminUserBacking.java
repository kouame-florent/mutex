/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.root;

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
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
import quantum.mutex.domain.entity.AdminUser;
import quantum.mutex.domain.entity.RoleName;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserRole;
import quantum.mutex.domain.entity.UserStatus;
import quantum.mutex.domain.dao.AdminUserDAO;
import quantum.mutex.domain.dao.RoleDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserRoleDAO;
import quantum.mutex.service.EncryptionService;
import quantum.mutex.domain.service.AdminUserService;
import quantum.mutex.domain.service.UserRoleService;
import quantum.mutex.domain.service.UserService;


/**
 *
 * @author Florent
 */
@Named(value = "editAdminUserBacking")
@ViewScoped
public class EditAdminUserBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(EditAdminUserBacking.class.getName());
    
    
    @Inject AdminUserDAO adminUserDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserRoleDAO userRoleDAO;
    @Inject UserDAO userDAO;
    @Inject UserRoleService userRoleService;
    @Inject AdminUserService adminUserService;
//    @Inject EncryptionService encryptionService;
   
    private AdminUser currentAdminUser;
    
    private final ViewParamKey adminUserParamKey = ViewParamKey.ADMIN_UUID;
    private String adminUserUUID;
    private ViewState viewState;
       
    public void viewAction(){
       viewState = updateViewState(adminUserUUID);
       Function<String, AdminUser> initAdmin = presetConfirmPassword.compose(retrieveAdminUser);
       currentAdminUser = initAdmin.apply(adminUserUUID);
    }
       
    Function<String, AdminUser> retrieveAdminUser = uuidStr -> Result.of(uuidStr)
                .map(UUID::fromString).flatMap(adminUserDAO::findById)
                .getOrElse(() -> new AdminUser());
    
    Function<AdminUser, AdminUser> presetConfirmPassword = adminUser -> {
        adminUser.setConfirmPassword(adminUser.getPassword());
        return adminUser;
    };
 
    
    private ViewState updateViewState(String adminUserUUID){
        return StringUtils.isBlank(adminUserUUID) ? ViewState.CREATE
                : ViewState.UPDATE;
    }
        
    public void persist(){  
        if(isPasswordValid(currentAdminUser)){
//          Result<UserRole> userRole = Result.of(currentAdminUser)
//                   .flatMap(this::persistAdmin).flatMap(this::persistUserRole);
//          userRole.flatMap(ur -> userDAO.findByLogin(ur.getUserLogin()))
//                  .forEach(returnToCaller);

             Result<AdminUser> user = Result.of(currentAdminUser)
                   .flatMap(this::persistAdmin);
             user.map(userRoleService::persistUserRole);
             user.forEach(u -> returnToCaller.apply(u));
        }else{
            showInvalidPasswordMessage();
        }

    }
    
        
    private Result<AdminUser> persistAdmin(@NotNull AdminUser adminUser){
        adminUser.setPassword(EncryptionService.hash(adminUser.getPassword()));
        adminUser.setStatus(UserStatus.DISABLED);
        return adminUserDAO.makePersistent(adminUser);
    }
    
//    private Result<UserRole> persistUserRole(@NotNull AdminUser adminUser){
//        return roleDAO.findByName(RoleName.ADMINISTRATOR)
//                    .map(role -> { return new UserRole(adminUser, role);} )
//                    .flatMap(userRoleDAO::makePersistent);
//     }
    
    private boolean isPasswordValid(@NotNull User user){
        return user.getPassword().equals(user.getConfirmPassword());
    }
     
    private void showInvalidPasswordMessage(){
        addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
    }
   
    
    private final Effect<User> returnToCaller = (user) ->
            PrimeFaces.current().dialog().closeDynamic(user);
     
    public void close(){
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public String getAdminUserUUID() {
        return adminUserUUID;
    }

    public void setAdminUserUUID(String adminUserUUID) {
        this.adminUserUUID = adminUserUUID;
    }

    public ViewState getViewState() {
        return viewState;
    }

    public AdminUser getCurrentAdminUser() {
        return currentAdminUser;
    }

    public void setCurrentAdminUser(AdminUser currentAdminUser) {
        this.currentAdminUser = currentAdminUser;
    }

    public ViewParamKey getAdminUserParamKey() {
        return adminUserParamKey;
    }

}
