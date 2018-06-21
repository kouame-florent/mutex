/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.dao.AdminUserDAO;


/**
 *
 * @author Florent
 */
@Named(value = "editAdminUserBacking")
@RequestScoped
public class EditAdminUserBacking extends BaseBacking implements Serializable{
    
    @Inject AdminUserDAO adminUserDAO;
   
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
    
    public void persist(){  
       AdminUser persistentAdminUser = adminUserDAO.makePersistent(currentAdminUser);
       PrimeFaces.current().dialog().closeDynamic(persistentAdminUser);
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

    
}
