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
import io.mutex.user.entity.Admin;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.service.UserRoleService;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.valueobject.ViewState;
import io.mutex.user.service.AdminService;


/**
 *
 * @author Florent
 */
@Named(value = "editAdminBacking")
@ViewScoped
public class EditAdminBacking extends QuantumEditBacking<Admin> implements Serializable{

   
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(EditAdminBacking.class.getName());
    
    @Inject UserRoleService userRoleService;
    @Inject AdminService adminService;
  
    private Admin currentAdmin;
    private final ContextIdParamKey adminParamKey = ContextIdParamKey.ADMIN_UUID;
    //private String adminUUID;
       
    @Override
    public void viewAction(){
       currentAdmin = initEntity(entityUUID);
       viewState = initViewState(entityUUID);
       currentAdmin = presetConfirmPassword(currentAdmin);
    }
    
    @Override
    protected Admin initEntity(String entityUUID) {
         return Optional.ofNullable(entityUUID)
                .flatMap(adminService::findByUuid)
                .orElseGet(() -> new Admin());
    }

    @Override
    public void edit() {
        switch(viewState){
             case CREATE:
             {
                 try {
                    adminService.createAdmin(currentAdmin)
                            .flatMap(adminService::createAdminRole)
                            .flatMap(usr -> adminService.findByLogin(usr.getUserLogin()))
                            .ifPresent(this::returnToCaller);
                 } catch (AdminExistException | NotMatchingPasswordAndConfirmation ex) {
                     addGlobalErrorMessage(ex.getMessage());
                 }
             }
             break;
             case UPDATE:
			try {
				adminService.updateAdmin(currentAdmin)
				   .ifPresent(this::returnToCaller);
			} catch (AdminLoginExistException | NotMatchingPasswordAndConfirmation e) {
				addGlobalErrorMessage(e.getMessage());
			}
			break;
                 

         }
        
        
//        
//        Optional<Admin> oAdmin = adminService.createAdminAndRole(currentAdmin);
//        if(oAdmin.isPresent()){
//            returnToCaller(oAdmin.get());
//        }else{
//            showInvalidPasswordMessage();
//        }
    }
    
    private Admin presetConfirmPassword(Admin admin){
        admin.setConfirmPassword(admin.getPassword());
        return admin;
    }
        
//    private void showInvalidPasswordMessage(){
//        addMessageFromResourceBundle(null, "user.password.validation.error", 
//                FacesMessage.SEVERITY_ERROR);
//    }
     
    public void close(){
        PrimeFaces.current().dialog().closeDynamic(null);
    }

//    public String getAdminUUID() {
//        return adminUUID;
//    }
//
//    public void setAdminUUID(String adminUUID) {
//        this.adminUUID = adminUUID;
//    }

    public ViewState getViewState() {
        return viewState;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public void setCurrentAdmin(Admin currentAdmin) {
        this.currentAdmin = currentAdmin;
    }

    public ContextIdParamKey getAdminParamKey() {
        return adminParamKey;
    }
}
