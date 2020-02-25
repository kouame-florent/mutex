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
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.valueobject.TenantStatus;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.valueobject.ViewID;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.exception.TenantNameExistException;
import java.util.Optional;
import io.mutex.user.service.AdminService;
import io.mutex.user.service.SpaceService;


/**
 *
 * @author Florent
 */
@Named(value = "tenantBacking")
@ViewScoped
public class TenantBacking extends QuantumMainBacking<Space> implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(TenantBacking.class.getName());

    @Inject SpaceService tenantService;
    @Inject AdminService adminUserService;
  
    private Admin selectedAdminUser;
    private final Set<Admin> selectedAdminUsers = new HashSet<>();

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
   
    private void updateAndRefresh(Space tenant){
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
   
    public void openAddAdminDialog(Space tenant){
        Map<String,Object> options = getDialogOptions(65, 70,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.ADD_ADMIN_DIALOG.id(), options, 
                        getDialogParams(ContextIdParamKey.TENANT_UUID,
                                tenant.getUuid()));
        LOG.log(Level.INFO, "-- TENANT UUID:{0}", tenant.getUuid());
    }  
    
    public void unlinkAdmin(Space tenant){
        tenantService.unlinkAdminAndChangeStatus(tenant);
    }  
    
    public void disableTenant( Space tenant){
        tenantService.changeStatus(tenant, TenantStatus.DISABLED);
        updateAndRefresh(tenant);
    }
    
    public void enableTenant( Space tenant){
        tenantService.changeStatus(tenant, TenantStatus.ENABLED);
        updateAndRefresh(tenant);
    }
        
    public void disableAdmin(Space tenant){
        changeAdminStatus(tenant, UserStatus.DISABLED);

    }
    
    public void enableAdmin(Space tenant){
         changeAdminStatus(tenant, UserStatus.ENABLED);
      
    }
    
    private void changeAdminStatus(Space tenant,UserStatus status){
        adminUserService.findBySpace(tenant)
                .flatMap(adm -> adminUserService.changeAdminUserStatus(adm, status))
                .ifPresent(this::updateAdminUser_);
    }
    
    private Optional<Admin> updateAdminUser_(Admin adminUser){
       try {
           return  adminUserService.updateAdminUser(adminUser);
       } catch (AdminLoginExistException | NotMatchingPasswordAndConfirmation ex) {
           addGlobalErrorMessage(ex.getMessage());
       }
       
       return Optional.empty();
    }
    
    public boolean rendererAssociateAdminLink(Space tenant){
       return adminUserService.findBySpace(tenant).isEmpty();
    }
    
    public boolean rendererRemoveAssociationLink(Space tenant){
        return adminUserService.findBySpace(tenant).isPresent();
    }
   
    public boolean rendererEnableTenantLink(Space tenant){
        return tenant.getStatus().equals(TenantStatus.DISABLED);
    }
    
     public boolean rendererDisableTenantLink(Space tenant){
        return tenant.getStatus().equals(TenantStatus.ENABLED);
    }
    
    public boolean rendererEnableAdminLink(Space tenant){
        return adminUserService.findBySpace(tenant).stream()
                .filter(adm -> adm.getStatus().equals(UserStatus.DISABLED))
                .count() > 0;
    }
    
    public boolean rendererDisableAdminLink( Space tenant){
        return adminUserService.findBySpace(tenant).stream()
                .filter(adm -> adm.getStatus().equals(UserStatus.ENABLED))
                .count() > 0;
    }

    public void handleEditTenantReturn(SelectEvent event){
       LOG.log(Level.INFO, "---> RETURN FROM HANDLE ADD TENZNT...");
       initTenants();
       selectedEntity = (Space)event.getObject();
    }
  
    public void handleSetAdminReturn(SelectEvent event){
       selectedAdminUser = (Admin)event.getObject();
       LOG.log(Level.INFO, "--- HANDLE SELECTED ADMIN: {0}", selectedAdminUser);
    }
   
    public void updateTenant( Space tenant){
       LOG.log(Level.INFO, "--- UPDATE SELECTED ADMIN: {0}", selectedAdminUser);
       if(selectedAdminUser != null){
           try {
               tenantService.updateTenantAdmin(tenant, selectedAdminUser);
           } catch (AdminUserExistException | NotMatchingPasswordAndConfirmation ex) {
               addGlobalErrorMessage(ex.getMessage());
           }
       }
   }
   
   public String retrieveAdmin(Space tenant){
     return adminUserService.findBySpace(tenant)
             .map(Admin::getName).orElse("");
     
   }
   
   public String retrieveAdminLogin(Space tenant){
     return adminUserService.findBySpace(tenant)
             .map(Admin::getLogin).orElse("");
   }
   
   public String retrieveAdminStatus(Space tenant){
    return adminUserService.findBySpace(tenant)
             .map(Admin::getStatus).map(Object::toString).orElse("");
     
   }
 
    public boolean rendererAction( Admin adminUser){
        return selectedAdminUsers.contains(adminUser);
    }
         
    public void check( Admin adminUser){   
       selectedAdminUsers.add(adminUser);
        
    }
    
    public void uncheck( Admin adminUser){
       selectedAdminUsers.remove(adminUser);
   }
    
    public void provideSelectedTenant(Space tenant){
        selectedEntity = tenant;
    }

    public void handleDialogClose(CloseEvent closeEvent){
        initTenants();
    }
      
    public Admin getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public Set<Admin> getSelectedAdminUsers() {
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
