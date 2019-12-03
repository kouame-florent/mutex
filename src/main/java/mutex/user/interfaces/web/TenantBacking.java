/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.interfaces.web;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import mutex.shared.interfaces.web.BaseBacking;
import mutex.shared.interfaces.web.ViewID;
import mutex.shared.interfaces.web.ViewParamKey;
import mutex.user.domain.entity.AdminUser;
import mutex.user.domain.entity.Tenant;
import mutex.user.domain.valueobject.TenantStatus;
import mutex.user.domain.valueobject.UserStatus;
import mutex.user.repository.AdminUserDAO;
import mutex.user.repository.TenantDAO;
import mutex.user.service.AdminUserService;
import mutex.user.service.TenantService;


/**
 *
 * @author Florent
 */
@Named(value = "tenantBacking")
@ViewScoped
public class TenantBacking extends BaseBacking implements Serializable{
    
   private static final Logger LOG = Logger.getLogger(TenantBacking.class.getName());
   
   @Inject TenantDAO tenantDAO;
   @Inject AdminUserDAO adminUserDAO;
   @Inject TenantService tenantService;
   @Inject AdminUserService adminUserService;
  
   private Tenant selectedTenant;
   private AdminUser selectedAdminUser;
   private final Set<AdminUser> selectedAdminUsers = new HashSet<>();
   private List<Tenant> tenants = Collections.EMPTY_LIST;
   
   @PostConstruct
   public void init(){
      initTenants();
   }
   
    private void initTenants() {
       tenants = tenantDAO.findAll();
    }
    
    private Tenant updateAndRefresh( Tenant tenant){
        Optional<Tenant> mTenant = tenantDAO.makePersistent(tenant);
        initTenants();
        return mTenant.orElseGet(() -> new Tenant());
    }
   
   public void openAddTenantDialog(){
        Map<String,Object> options = getDialogOptions(55, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_TENANT_DIALOG.id(), options, null);
   }
   
   public void openEditTenantDialog( Tenant tenant){
        Map<String,Object> options = getDialogOptions(60, 50,true);
        PrimeFaces.current().dialog().openDynamic("edit-tenant-dlg", options, 
                        getDialogParams(ViewParamKey.TENANT_UUID, 
                                tenant.getUuid()));
   }
   
   public void openAddAdmintDialog(){
        Map<String,Object> options = getDialogOptions(65, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-administrator-dlg", options, null);
   }
   
    public void openSetAdminDialog( Tenant tenant){
        
        Map<String,Object> options = getDialogOptions(65, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.CHOOSE_ADMIN_DIALOG.id(), options, 
                        getDialogParams(ViewParamKey.TENANT_UUID,
                                tenant.getUuid()));
        LOG.log(Level.INFO, "-- TENANT UUID:{0}", tenant.getUuid());
    }  
    
    public void disableTenant( Tenant tenant){
        tenant.setStatus(TenantStatus.DISABLED);
        updateAndRefresh(tenant);
    }
    
    public void enableTenant( Tenant tenant){
        tenant.setStatus(TenantStatus.ENABLED);
        updateAndRefresh(tenant);
    }
   
     
    public void disableAdmin( Tenant tenant){
        adminUserDAO.findByTenant(tenant).stream()
                .map(this.updateStatus)
                .map(f -> f.apply(UserStatus.DISABLED))
                .forEach(adminUserDAO::makePersistent);
    }
    
    public void enableAdmin( Tenant tenant){
        adminUserDAO.findByTenant(tenant).stream()
                .map(this.updateStatus)
                .map(f -> f.apply(UserStatus.ENABLED))
                .forEach(adminUserDAO::makePersistent);
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
        return adminUserDAO.findByTenant(tenant).stream()
                .filter(adm -> adm.getStatus().equals(UserStatus.DISABLED))
                .count() > 0;
    }
    
    public boolean rendererDisableAdminLink( Tenant tenant){
        return adminUserDAO.findByTenant(tenant).stream()
                .filter(adm -> adm.getStatus().equals(UserStatus.ENABLED))
                .count() > 0;
    }

   public void handleEditTenantReturn(SelectEvent event){
       LOG.log(Level.INFO, "---> RETURN FROM HANDLE ADD TENZNT...");
       initTenants();
       selectedTenant = (Tenant)event.getObject();
   
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
     return (!adminUserDAO.findByTenant(tenant).isEmpty()) 
              ? adminUserDAO.findByTenant(tenant).get(0).getName() : "";
   }
   
   public String retrieveAdminLogin( Tenant tenant){
     return (!adminUserDAO.findByTenant(tenant).isEmpty()) 
              ? adminUserDAO.findByTenant(tenant).get(0).getLogin() : "";
   }
   
   public String retrieveAdminStatus( Tenant tenant){
     if( (!adminUserDAO.findByTenant(tenant).isEmpty()) && 
             (adminUserDAO.findByTenant(tenant).get(0).getStatus() != null) ){
         return adminUserDAO.findByTenant(tenant).get(0).getStatus().getValue();
     }
     return "";      
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
        selectedTenant = tenant;
    }
    
    public void processDeleteTenant(){
        if(selectedTenant != null){
            resetAdminTenant(selectedTenant);
            tenantDAO.makeTransient(selectedTenant);     
        }
       
    }
    
    private void resetAdminTenant( Tenant tenant){
        adminUserDAO.findByTenant(tenant)
                .stream().forEach(adminUserService::resetTenant);
    }
    
    public void handleDialogClose(CloseEvent closeEvent){
        initTenants();
    }
   
    public Tenant getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(Tenant selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

   
    public AdminUser getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public Set<AdminUser> getSelectedAdminUsers() {
        return selectedAdminUsers;
    }
   
}
