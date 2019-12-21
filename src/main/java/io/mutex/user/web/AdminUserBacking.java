/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.service.AdminUserService;
import io.mutex.user.service.UserRoleService;

/**
 *
 * @author Florent
 */
@Named(value = "adminUserBacking")
@ViewScoped
public class AdminUserBacking extends QuantumBacking<AdminUser> implements Serializable{
        
    @Inject AdminUserService adminUserService;
    @Inject UserRoleService userRoleService;
  
    private final ViewParamKey currentViewParamKey = ViewParamKey.ADMIN_UUID;
    
    @PostConstruct
    public void init(){
       initAdminUsers();
    }
   
    @Override
    public void delete() {
        if(selectedEntity != null){
           adminUserService.deleteTenant(selectedEntity);
       }
    }

    @Override
    protected String viewId() {
        return ViewID.EDIT_ADMINISTRATOR_DIALOG.id();
    }
   
    private void initAdminUsers(){
         entities = initView(adminUserService::findAllAdminUsers);
    }
      
    public void handleEditAdminUserReturn(SelectEvent event){
        initAdminUsers();
        userRoleService.cleanOrphanLogins();
    }
   
    public void provideSelectedAdminUser( AdminUser adminUser){
        selectedEntity = adminUser;
    }

    public void handleDialogClose(CloseEvent closeEvent){
        initAdminUsers();
    }
   
    public String retrieveTenant( AdminUser adminUser){
       return (adminUser.getTenant() != null) ? adminUser.getTenant().getName() : "";
    }

    public void handleAddAdminUserReturn(SelectEvent event){
        initAdminUsers();
        selectedEntity = (AdminUser)event.getObject();
    }
   
    public ViewParamKey getCurrentViewParamKey() {
        return currentViewParamKey;
    }
        
}
