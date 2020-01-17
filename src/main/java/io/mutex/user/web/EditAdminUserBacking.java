/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.service.AdminUserService;
import io.mutex.user.service.UserRoleService;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.valueobject.ViewState;


/**
 *
 * @author Florent
 */
@Named(value = "editAdminUserBacking")
@ViewScoped
public class EditAdminUserBacking extends QuantumEditBacking<AdminUser> implements Serializable{

   
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(EditAdminUserBacking.class.getName());
    
    @Inject UserRoleService userRoleService;
    @Inject AdminUserService adminUserService;
  
    private AdminUser currentAdminUser;
    private final ContextIdParamKey adminUserParamKey = ContextIdParamKey.ADMIN_UUID;
    //private String adminUserUUID;
       
    @Override
    public void viewAction(){
       currentAdminUser = initEntity(entityUUID);
       viewState = initViewState(entityUUID);
       currentAdminUser = presetConfirmPassword(currentAdminUser);
    }
    
    @Override
    protected AdminUser initEntity(String entityUUID) {
         return Optional.ofNullable(entityUUID)
                .flatMap(adminUserService::findByUuid)
                .orElseGet(() -> new AdminUser());
    }

    @Override
    public void edit() {
        switch(viewState){
             case CREATE:
             {
                 try {
                    adminUserService.createAdminUser(currentAdminUser)
                            .flatMap(adminUserService::createAdminUserRole)
                            .flatMap(usr -> adminUserService.findByLogin(usr.getUserLogin()))
                            .ifPresent(this::returnToCaller);
                 } catch (AdminUserExistException | NotMatchingPasswordAndConfirmation ex) {
                     addGlobalErrorMessage(ex.getMessage());
                 }
             }
             break;
             case UPDATE:
			try {
				adminUserService.updateAdminUser(currentAdminUser)
				   .ifPresent(this::returnToCaller);
			} catch (AdminLoginExistException | NotMatchingPasswordAndConfirmation e) {
				addGlobalErrorMessage(e.getMessage());
			}
			break;
                 

         }
        
        
//        
//        Optional<AdminUser> oAdminUser = adminUserService.createAdminUserAndRole(currentAdminUser);
//        if(oAdminUser.isPresent()){
//            returnToCaller(oAdminUser.get());
//        }else{
//            showInvalidPasswordMessage();
//        }
    }
    
    private AdminUser presetConfirmPassword(AdminUser adminUser){
        adminUser.setConfirmPassword(adminUser.getPassword());
        return adminUser;
    }
        
//    private void showInvalidPasswordMessage(){
//        addMessageFromResourceBundle(null, "user.password.validation.error", 
//                FacesMessage.SEVERITY_ERROR);
//    }
     
    public void close(){
        PrimeFaces.current().dialog().closeDynamic(null);
    }

//    public String getAdminUserUUID() {
//        return adminUserUUID;
//    }
//
//    public void setAdminUserUUID(String adminUserUUID) {
//        this.adminUserUUID = adminUserUUID;
//    }

    public ViewState getViewState() {
        return viewState;
    }

    public AdminUser getCurrentAdminUser() {
        return currentAdminUser;
    }

    public void setCurrentAdminUser(AdminUser currentAdminUser) {
        this.currentAdminUser = currentAdminUser;
    }

    public ContextIdParamKey getAdminUserParamKey() {
        return adminUserParamKey;
    }
}
