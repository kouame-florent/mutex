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
import io.mutex.user.service.impl.AdminUserServiceImpl;
import io.mutex.user.service.impl.UserRoleServiceImpl;
import io.mutex.user.valueobject.ViewID;
import io.mutex.user.valueobject.ContextIdParamKey;

/**
 *
 * @author Florent
 */
@Named(value = "adminUserBacking")
@ViewScoped
public class AdminUserBacking extends QuantumMainBacking<AdminUser> implements Serializable{
        
    private static final long serialVersionUID = 1L;
	
    @Inject AdminUserServiceImpl adminUserService;
    @Inject UserRoleServiceImpl userRoleService;
  
//    private final ContextIdParamKey currentViewParamKey = ContextIdParamKey.ADMIN_UUID;
    
    
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

    @Override
    protected String deleteViewId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
    
}
