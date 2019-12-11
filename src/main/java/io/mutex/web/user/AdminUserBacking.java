/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web.user;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import io.mutex.domain.entity.AdminUser;
import io.mutex.user.service.AdminUserService;
import io.mutex.user.service.UserRoleService;
import io.mutex.web.ViewID;
import io.mutex.web.ViewParamKey;

/**
 *
 * @author Florent
 */
@Named(value = "adminUserBacking")
@ViewScoped
public class AdminUserBacking extends QuantumBacking<AdminUser> implements Serializable{
        
//    @Inject AdminUserDAO adminUserDAO;
//    @Inject TenantDAO tenantDAO;
    @Inject AdminUserService adminUserService;
    @Inject UserRoleService userRoleService;
    
    private AdminUser selectedAdminUser;
    private List<AdminUser> adminUsers ;
    
    private final ViewParamKey currentViewParamKey = ViewParamKey.ADMIN_UUID;
    
    @PostConstruct
    public void init(){
       initAdminUsers();
    }
   
    @Override
    public void delete() {
        if(selectedAdminUser != null){
           adminUserService.deleteTenant(selectedAdminUser);
       }
    }

    @Override
    protected String viewId() {
        return ViewID.EDIT_ADMINISTRATOR_DIALOG.id();
    }
   
    private void initAdminUsers(){
         adminUsers = initView(adminUserService::findAllAdminUsers);
    }
   
//   public void openAddAdminUserDialog(){
//        Map<String,Object> options = getDialogOptions(65, 70,true);
//        PrimeFaces.current().dialog()
//                .openDynamic(ViewID.EDIT_ADMINISTRATOR_DIALOG.id(), options, null);
//   }
   
//   public void openEditAdminUserDialog( AdminUser adminUser){
//        Map<String,Object> options = getDialogOptions(65, 70,true);
//        PrimeFaces.current().dialog()
//                .openDynamic(ViewID.EDIT_ADMINISTRATOR_DIALOG.id(), options, 
//                        getDialogParams(ViewParamKey.ADMIN_UUID, 
//                                adminUser.getUuid()));
//   }
   
   public void handleEditAdminUserReturn(SelectEvent event){
       initAdminUsers();
       userRoleService.cleanOrphanLogins();
   }
   
    public void provideSelectedAdminUser( AdminUser adminUser){
        selectedAdminUser = adminUser;
    }
   
//    public void deleteAdminUser(){
//       if(selectedAdminUser != null){
//           adminUserDAO.makeTransient(selectedAdminUser);
//       }
//    }
   
    public void handleDialogClose(CloseEvent closeEvent){
        initAdminUsers();
    }
   
   public String retrieveTenant( AdminUser adminUser){
      return (adminUser.getTenant() != null) ? adminUser.getTenant().getName() : "";
   }

   public void handleAddAdminUserReturn(SelectEvent event){
       initAdminUsers();
       selectedAdminUser = (AdminUser)event.getObject();
   }
   
    public List<AdminUser> getAdminUsers() {
        return adminUsers;
    }

    public AdminUser getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public void setSelectedAdminUser(AdminUser selectedAdminUser) {
        this.selectedAdminUser = selectedAdminUser;
    }

    public ViewParamKey getCurrentViewParamKey() {
        return currentViewParamKey;
    }
        
}
