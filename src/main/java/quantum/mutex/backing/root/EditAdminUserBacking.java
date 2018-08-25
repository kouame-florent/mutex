/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.root;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.User;
import quantum.mutex.domain.dao.AdminUserDAO;
import quantum.mutex.service.EncryptionService;


/**
 *
 * @author Florent
 */
@Named(value = "editAdminUserBacking")
@RequestScoped
public class EditAdminUserBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(EditAdminUserBacking.class.getName());
    
    
    @Inject AdminUserDAO adminUserDAO;
    @Inject EncryptionService encryptionService;
   
    private AdminUser currentAdminUser;
    
    private final ViewParamKey adminUserParamKey = ViewParamKey.ADMIN_UUID;
    private String adminUserUUID;
    private ViewState viewState = ViewState.CREATE;
    
    @PostConstruct
    public void init(){
        currentAdminUser = new AdminUser();
    }
    
    public void viewAction(){
        if(!StringUtils.isBlank(adminUserUUID)){
            viewState = ViewState.UPDATE;
            currentAdminUser = adminUserDAO.findById(UUID.fromString(adminUserUUID));
        }
    }
   
    private boolean checkBoxValue;
    
    public boolean showPasswordCheckbox(){
        return viewState == ViewState.UPDATE;
    }
    
    public boolean showPasswordInputs(){
        return (viewState == ViewState.CREATE) 
                || ( (viewState == ViewState.UPDATE) && checkBoxValue);
    }
        
    public void persist(){  
       if(isPasswordValid(currentAdminUser)){
           currentAdminUser.setPassword(encryptionService.hash(currentAdminUser.getPassword()));
           AdminUser persistentAdminUser = adminUserDAO.makePersistent(currentAdminUser);
           PrimeFaces.current().dialog().closeDynamic(persistentAdminUser);
       }
       
    }
    
     private boolean isPasswordValid(@NotNull User user){
        boolean result = user.getPassword().equals(user.getConfirmPassword());
        if( (StringUtils.isBlank(adminUserUUID)) || (!result) ){
            addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
        }
        return result;
    }
    
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


    public boolean isCheckBoxValue() {
        return checkBoxValue;
    }

    public void setCheckBoxValue(boolean checkBoxValue) {
        this.checkBoxValue = checkBoxValue;
    }

    
}
