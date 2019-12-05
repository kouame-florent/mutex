/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import io.mutex.domain.entity.AdminUser;
import io.mutex.repository.AdminUserDAO;
import io.mutex.repository.TenantDAO;
import io.mutex.service.UserRoleService;

/**
 *
 * @author Florent
 */
@Named(value = "adminUserBacking")
@ViewScoped
public class AdminUserBacking extends BaseBacking implements Serializable{
    
    
    @Inject AdminUserDAO adminUserDAO;
    @Inject TenantDAO tenantDAO;
    @Inject UserRoleService userRoleService;
    
    private AdminUser selectedAdminUser;
    private List<AdminUser> adminUsers ;
    
   @PostConstruct
   public void init(){
      findAllAdminUsers();
   }
   
   private void findAllAdminUsers(){
       adminUsers = adminUserDAO.findAll();
   }
   
   public void openAddAdminUserDialog(){
        Map<String,Object> options = getDialogOptions(65, 70,true);
        
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_ADMINISTRATOR_DIALOG.id(), options, null);
   }
   
   public void openEditAdminUserDialog( AdminUser adminUser){
        Map<String,Object> options = getDialogOptions(65, 70,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_ADMINISTRATOR_DIALOG.id(), options, 
                        getDialogParams(ViewParamKey.ADMIN_UUID, 
                                adminUser.getUuid()));
   }
   
   public void handleEditAdminUserReturn(SelectEvent event){
       findAllAdminUsers();
       userRoleService.cleanOrphanLogins();
   }
   
   public void provideSelectedAdminUser( AdminUser adminUser){
       selectedAdminUser = adminUser;
   }
   
    public void deleteAdminUser(){
       if(selectedAdminUser != null){
           adminUserDAO.makeTransient(selectedAdminUser);
       }
   }
   
    public void handleDialogClose(CloseEvent closeEvent){
        findAllAdminUsers();
    }
   
   public String retrieveTenant( AdminUser adminUser){
      return (adminUser.getTenant() != null) ? adminUser.getTenant().getName() : "";
   }

   public void handleAddAdminUserReturn(SelectEvent event){
       findAllAdminUsers();
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

   
   
}
