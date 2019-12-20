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
import java.util.function.Function;
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
import io.mutex.user.valueobject.TenantStatus;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.exception.TenantNameExistException;
import io.mutex.user.service.AdminUserService;
import io.mutex.user.service.TenantService;


/**
 *
 * @author Florent
 */
@Named(value = "tenantBacking")
@ViewScoped
public class TenantBacking extends QuantumBacking<Tenant> implements Serializable{
    
   private static final Logger LOG = Logger.getLogger(TenantBacking.class.getName());

   @Inject TenantService tenantService;
   @Inject AdminUserService adminUserService;
  
  // private Tenant selectedTenant;
   private AdminUser selectedAdminUser;
   private final Set<AdminUser> selectedAdminUsers = new HashSet<>();
//   private List<Tenant> tenants = Collections.EMPTY_LIST;
   
   private final ViewParamKey currentViewParamKey = ViewParamKey.TENANT_UUID;
   
    @PostConstruct
    public void init(){
       initTenants();
    }
   
    private void initTenants() {
       entities = initView(tenantService::findAllTenants);
    }
    
    @Override
    protected String viewId() {
        return ViewID.EDIT_TENANT_DIALOG.id();
    }
    
    @Override
    public void delete() {
        tenantService.deleteTenant(selectedEntity);
    }
   
    private void updateAndRefresh(Tenant tenant){
        try {
           tenantService.updateTenant(tenant);
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
   
    public void openLinkAdminDialog( Tenant tenant){
        Map<String,Object> options = getDialogOptions(65, 70,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.LINK_ADMIN_DIALOG.id(), options, 
                        getDialogParams(ViewParamKey.TENANT_UUID,
                                tenant.getUuid()));
        LOG.log(Level.INFO, "-- TENANT UUID:{0}", tenant.getUuid());
    }  
    
    public void disableTenant( Tenant tenant){
//        tenant.setStatus(TenantStatus.DISABLED);
        tenantService.disableTenant(tenant);
        updateAndRefresh(tenant);
    }
    
    public void enableTenant( Tenant tenant){
        tenant.setStatus(TenantStatus.ENABLED);
        updateAndRefresh(tenant);
    }
        
    public void disableAdmin( Tenant tenant){
        adminUserService.findByTenant(tenant).stream()
                .map(this.updateStatus)
                .map(f -> f.apply(UserStatus.DISABLED))
                .forEach(adminUserService::updateAdminUser);
    }
    
    public void enableAdmin( Tenant tenant){
        adminUserService.findByTenant(tenant).stream()
                .map(this.updateStatus)
                .map(f -> f.apply(UserStatus.ENABLED))
                .forEach(adminUserService::updateAdminUser);
    }
        
    private final Function<AdminUser,Function<UserStatus, AdminUser>> updateStatus = 
            admin -> status -> { 
                admin.setStatus(status);
                return admin;
            };
    
    public boolean rendererEnableTenantLink( Tenant tenant){
        return tenant.getStatus().equals(TenantStatus.DISABLED);
    }
    
     public boolean rendererDisableTenantLink( Tenant tenant){
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
           tenantService.updateTenantAdmin(tenant, selectedAdminUser);
       }
   }
   
   public String retrieveAdmin( Tenant tenant){
     return adminUserService.findByTenant(tenant)
             .map(AdminUser::getName).orElse("");
     
   }
   
   public String retrieveAdminLogin( Tenant tenant){
     return adminUserService.findByTenant(tenant)
             .map(AdminUser::getLogin).orElse("");
   }
   
   public String retrieveAdminStatus( Tenant tenant){
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
    
    public void provideSelectedTenant( Tenant tenant){
        selectedEntity = tenant;
    }
        
//    private void changeAdminStatus(Tenant tenant){
//        adminUserService.findByTenant(tenant)
//                .stream().forEach(adminUserService::changeAdminUserStatus);
//    }
    
    public void handleDialogClose(CloseEvent closeEvent){
        initTenants();
    }
   
//    public Tenant getSelectedTenant() {
//        return selectedTenant;
//    }
//
//    public void setSelectedTenant(Tenant selectedTenant) {
//        this.selectedTenant = selectedTenant;
//    }

//    public List<Tenant> getTenants() {
//        return tenants;
//    }
   
    public AdminUser getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public Set<AdminUser> getSelectedAdminUsers() {
        return selectedAdminUsers;
    }

    public ViewParamKey getCurrentViewParamKey() {
        return currentViewParamKey;
    }

}
