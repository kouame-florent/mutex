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
import io.mutex.user.entity.Admin;
import io.mutex.user.service.UserRoleService;
import io.mutex.user.valueobject.ViewID;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.service.AdminService;

/**
 *
 * @author Florent
 */
@Named(value = "adminUserBacking")
@ViewScoped
public class AdminUserBacking extends QuantumMainBacking<Admin> implements Serializable{
        
    private static final long serialVersionUID = 1L;
	
    @Inject AdminService adminUserService;
    @Inject UserRoleService userRoleService;
    
    @Override
    @PostConstruct
    protected void postConstruct() {
        initCtxParamKey(ContextIdParamKey.ADMIN_UUID);
        initAdminUsers();
    }

//    @Override
    public void delete() {
       adminUserService.delete(selectedEntity);

    }

    @Override
    protected String editViewId() {
        return ViewID.EDIT_ADMINISTRATOR_DIALOG.id();
    }
   
    private void initAdminUsers(){
         initContextEntities(adminUserService::findAllAdminUsers);
    }
      
    public void handleEditAdminUserReturn(SelectEvent event){
        initAdminUsers();
        userRoleService.cleanOrphansUserRole();
    }
   
    public void provideSelectedAdminUser( Admin adminUser){
        selectedEntity = adminUser;
    }

    public void handleDialogClose(CloseEvent closeEvent){
        initAdminUsers();
    }
   
    public String retrieveTenant( Admin adminUser){
       return (adminUser.getTenant() != null) ? adminUser.getTenant().getName() : "";
    }

    public void handleAddAdminUserReturn(SelectEvent event){
        initAdminUsers();
        selectedEntity = (Admin)event.getObject();
    }

    @Override
    protected String deleteViewId() {
        return ViewID.DELETE_USER_DIALOG.id();
    }
   
    
}
