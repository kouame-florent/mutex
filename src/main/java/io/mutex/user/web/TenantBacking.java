/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.valueobject.TenantStatus;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.valueobject.ViewID;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.exception.TenantNameExistException;
import io.mutex.user.service.AdminUserService;
import io.mutex.user.service.TenantService;
import java.util.Optional;


/**
 *
 * @author Florent
 */
@Named(value = "tenantBacking")
@ViewScoped
public class TenantBacking extends QuantumBacking<Tenant> implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(TenantBacking.class.getName());

   @Inject TenantService tenantService;
   @Inject AdminUserService adminUserService;
  
    private AdminUser selectedAdminUser;
    private final Set<AdminUser> selectedAdminUsers = new HashSet<>();

    @Override
    @PostConstruct
    protected void postConstruct() {
       initCtxParamKey(ContextIdParamKey.TENANT_UUID);
       initTenants();
    }
   
    private void initTenants() {
       initContextEntities(tenantService::findAllTenants);
    }

    @Override
    protected String editViewId() {
        return ViewID.EDIT_TENANT_DIALOG.id();
    }
    
//    @Override
    public void delete() {
        tenantService.delete(selectedEntity);
    }
   
    private void updateAndRefresh(Tenant tenant){
        try {
           tenantService.update(tenant);
           initTenants();
        } catch (TenantNameExistException ex) {
           addGlobalErrorMessage(ex.getMessage());
        }
    }
    
    public void openAddAdmintDialog(){
        Map<String,Object> options = getDialogOptions(65, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-administrator-dlg", options, null);
    }
   
    public void openAddAdminDialog(Tenant tenant){
        Map<String,Object> options = getDialogOptions(65, 70,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.ADD_ADMIN_DIALOG.id(), options, 
                        getDialogParams(ContextIdParamKey.TENANT_UUID,
                                tenant.getUuid()));
        LOG.log(Level.INFO, "-- TENANT UUID:{0}", tenant.getUuid());
    }  
    
    public void unlinkAdmin(Tenant tenant){
        tenantService.unlinkAdminAndChangeStatus(tenant);
    }  
    
    public void disableTenant( Tenant tenant){
        tenantService.changeStatus(tenant, TenantStatus.DISABLED);
        updateAndRefresh(tenant);
    }
    
    public void enableTenant( Tenant tenant){
        tenantService.changeStatus(tenant, TenantStatus.ENABLED);
        updateAndRefresh(tenant);
    }
        
    public void disableAdmin(Tenant tenant){
        changeAdminStatus(tenant, UserStatus.DISABLED);

    }
    
    public void enableAdmin(Tenant tenant){
         changeAdminStatus(tenant, UserStatus.ENABLED);
      
    }
    
    private void changeAdminStatus(Tenant tenant,UserStatus status){
        adminUserService.findByTenant(tenant)
                .flatMap(adm -> adminUserService.changeAdminUserStatus(adm, status))
                .ifPresent(this::updateAdminUser_);
    }
    
    private Optional<AdminUser> updateAdminUser_(AdminUser adminUser){
       try {
           return  adminUserService.updateAdminUser(adminUser);
       } catch (AdminLoginExistException | NotMatchingPasswordAndConfirmation ex) {
           addGlobalErrorMessage(ex.getMessage());
       }
       
       return Optional.empty();
    }
    
    public boolean rendererAssociateAdminLink(Tenant tenant){
       return adminUserService.findByTenant(tenant).isEmpty();
    }
    
    public boolean rendererRemoveAssociationLink(Tenant tenant){
        return adminUserService.findByTenant(tenant).isPresent();
    }
   
    public boolean rendererEnableTenantLink(Tenant tenant){
        return tenant.getStatus().equals(TenantStatus.DISABLED);
    }
    
     public boolean rendererDisableTenantLink(Tenant tenant){
        return tenant.getStatus().equals(TenantStatus.ENABLED);
    }
    
    public boolean rendererEnableAdminLink(Tenant tenant){
        return adminUserService.findByTenant(tenant).stream()
                .filter(adm -> adm.getStatus().equals(UserStatus.DISABLED))
                .count() > 0;
    }
    
    public boolean rendererDisableAdminLink( Tenant tenant){
        return adminUserService.findByTenant(tenant).stream()
                .filter(adm -> adm.getStatus().equals(UserStatus.ENABLED))
                .count() > 0;
    }

   public void handleEditTenantReturn(SelectEvent event){
       LOG.log(Level.INFO, "---> RETURN FROM HANDLE ADD TENZNT...");
       initTenants();
       selectedEntity = (Tenant)event.getObject();
   }
  
    public void handleSetAdminReturn(SelectEvent event){
       selectedAdminUser = (AdminUser)event.getObject();
       LOG.log(Level.INFO, "--- HANDLE SELECTED ADMIN: {0}", selectedAdminUser);
    }
   
   public void updateTenant( Tenant tenant){
       LOG.log(Level.INFO, "--- UPDATE SELECTED ADMIN: {0}", selectedAdminUser);
       if(selectedAdminUser != null){
           try {
               tenantService.updateTenantAdmin(tenant, selectedAdminUser);
           } catch (AdminUserExistException | NotMatchingPasswordAndConfirmation ex) {
               addGlobalErrorMessage(ex.getMessage());
           }
       }
   }
   
   public String retrieveAdmin(Tenant tenant){
     return adminUserService.findByTenant(tenant)
             .map(AdminUser::getName).orElse("");
     
   }
   
   public String retrieveAdminLogin(Tenant tenant){
     return adminUserService.findByTenant(tenant)
             .map(AdminUser::getLogin).orElse("");
   }
   
   public String retrieveAdminStatus(Tenant tenant){
    return adminUserService.findByTenant(tenant)
             .map(AdminUser::getStatus).map(Object::toString).orElse("");
     
   }
 
    public boolean rendererAction( AdminUser adminUser){
        return selectedAdminUsers.contains(adminUser);
    }
         
    public void check( AdminUser adminUser){   
       selectedAdminUsers.add(adminUser);
        
    }
    
    public void uncheck( AdminUser adminUser){
       selectedAdminUsers.remove(adminUser);
   }
    
    public void provideSelectedTenant(Tenant tenant){
        selectedEntity = tenant;
    }

    public void handleDialogClose(CloseEvent closeEvent){
        initTenants();
    }
      
    public AdminUser getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public Set<AdminUser> getSelectedAdminUsers() {
        return selectedAdminUsers;
    }

    public ContextIdParamKey getContextIdParamKey() {
        return contextIdParamKey;
    }

    @Override
    protected String deleteViewId() {
        return ViewID.DELETE_TENANT_DIALOG.id();
    }

}
