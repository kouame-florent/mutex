/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.service.AdminUserService;
import io.mutex.user.service.UserRoleService;


/**
 *
 * @author Florent
 */
@Named(value = "editAdminUserBacking")
@ViewScoped
public class EditAdminUserBacking extends QuantumEditBacking<AdminUser> implements Serializable{

    private static final Logger LOG = Logger.getLogger(EditAdminUserBacking.class.getName());
    
    @Inject UserRoleService userRoleService;
    @Inject AdminUserService adminUserService;
  
    private AdminUser currentAdminUser;
    
    private final ViewParamKey adminUserParamKey = ViewParamKey.ADMIN_UUID;
    private String adminUserUUID;
       
    @Override
    public void viewAction(){
       currentAdminUser = initEntity(entityUUID);
       viewState = initViewState(entityUUID);
       currentAdminUser = presetConfirmPassword(currentAdminUser);
    }
    
    @Override
    protected AdminUser initEntity(String entityUUID) {
         return Optional.ofNullable(adminUserUUID)
                .flatMap(adminUserService::findByUuid)
                .orElseGet(() -> new AdminUser());
    }

    @Override
    public void edit() {
        Optional<AdminUser> oAdminUser = adminUserService.createAdminUserAndRole(currentAdminUser);
        if(oAdminUser.isPresent()){
            returnToCaller(oAdminUser.get());
        }else{
            showInvalidPasswordMessage();
        }
    }
    
    private AdminUser presetConfirmPassword(AdminUser adminUser){
        adminUser.setConfirmPassword(adminUser.getPassword());
        return adminUser;
    }
        
    private void showInvalidPasswordMessage(){
        addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
    }
       
//    private final Consumer<AdminUser> returnToCaller = (adminUser ) ->
//            PrimeFaces.current().dialog().closeDynamic(adminUser);
//     
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
