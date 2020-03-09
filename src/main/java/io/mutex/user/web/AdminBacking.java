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
import io.mutex.user.service.SpaceService;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Florent
 */
@Named(value = "adminBacking")
@ViewScoped
public class AdminBacking extends QuantumMainBacking<Admin> implements Serializable{
        
    private static final long serialVersionUID = 1L;
	
    @Inject AdminService adminService;
    @Inject UserRoleService userRoleService;
    @Inject SpaceService spaceService;
    
    @Override
    @PostConstruct
    protected void postConstruct() {
        initCtxParamKey(ContextIdParamKey.ADMIN_UUID);
        initAdmins();
    }

//    @Override
    public void delete() {
       adminService.delete(selectedEntity);

    }

    @Override
    protected String editViewId() {
        return ViewID.EDIT_ADMINISTRATOR_DIALOG.id();
    }
   
    private void initAdmins(){
         initContextEntities(adminService::findAllAdmins);
    }
      
    public void handleEditAdminReturn(SelectEvent event){
        initAdmins();
        userRoleService.cleanOrphansUserRole();
    }
   
    public void provideSelectedAdmin( Admin admin){
        selectedEntity = admin;
    }

    public void handleDialogClose(CloseEvent closeEvent){
        initAdmins();
    }
   
//    public String retrieveSpaceName(@NotNull Admin admin){
//       
//       return (admin.getGroup().getSpace().getName()!= null) ? admin.getGroup().getSpace().getName() : "";
//    }

    public void handleAddAdminReturn(SelectEvent event){
        initAdmins();
        selectedEntity = (Admin)event.getObject();
    }

    @Override
    protected String deleteViewId() {
        return ViewID.DELETE_USER_DIALOG.id();
    }
   
    
}
