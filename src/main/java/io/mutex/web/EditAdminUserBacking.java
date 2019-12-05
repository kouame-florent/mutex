/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import io.mutex.domain.entity.AdminUser;
import io.mutex.domain.entity.User;
import io.mutex.domain.valueobject.UserStatus;
import io.mutex.repository.AdminUserDAO;
import io.mutex.repository.RoleDAO;
import io.mutex.repository.UserDAO;
import io.mutex.repository.UserRoleDAO;
import io.mutex.domain.valueobject.RoleName;
import io.mutex.service.EncryptionService;
import io.mutex.service.AdminUserService;
import io.mutex.service.UserRoleService;



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
  
    private AdminUser currentAdminUser;
    
    private final ViewParamKey adminUserParamKey = ViewParamKey.ADMIN_UUID;
    private String adminUserUUID;
    private ViewState viewState;
       
    public void viewAction(){
       viewState = updateViewState(adminUserUUID);
       currentAdminUser = retrieveAdmin(adminUserUUID);
       currentAdminUser = presetConfirmPassword(currentAdminUser);
    }
        
    private AdminUser retrieveAdmin(String adminUserUUID){
        return Optional.ofNullable(adminUserUUID)
                .flatMap(adminUserDAO::findById)
                .orElseGet(() -> new AdminUser());

    }
    
    private AdminUser presetConfirmPassword(AdminUser adminUser){
        adminUser.setConfirmPassword(adminUser.getPassword());
        return adminUser;
    }
    
    private ViewState updateViewState(String adminUserUUID){
        return StringUtils.isBlank(adminUserUUID) ? ViewState.CREATE
                : ViewState.UPDATE;
    }
        
    public void persist(){  
        if(isPasswordValid(currentAdminUser)){
            Optional<AdminUser> user = Optional.of(currentAdminUser)
                   .flatMap(this::persistAdmin);
             user.map(u -> userRoleService.persistUserRole(u, RoleName.ADMINISTRATOR));
             user.ifPresent(u -> returnToCaller.accept(u));
        }else{
            showInvalidPasswordMessage();
        }

    }
         
    private Optional<AdminUser> persistAdmin( AdminUser adminUser){
        adminUser.setPassword(EncryptionService.hash(adminUser.getPassword()));
        adminUser.setStatus(UserStatus.DISABLED);
        return adminUserDAO.makePersistent(adminUser);
    }
 
    private boolean isPasswordValid( User user){
        return user.getPassword().equals(user.getConfirmPassword());
    }
     
    private void showInvalidPasswordMessage(){
        addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
    }
   
    
    private final Consumer<User> returnToCaller = (user) ->
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
