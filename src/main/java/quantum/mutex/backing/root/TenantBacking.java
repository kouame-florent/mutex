/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.root;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.TenantStatus;
import quantum.mutex.domain.dao.AdminUserDAO;
import quantum.mutex.domain.dao.TenantDAO;
import quantum.mutex.service.TenantService;

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
  
   private Tenant selectedTenant;
   private AdminUser selectedAdminUser;
   private final Set<AdminUser> selectedAdminUsers = new HashSet<>();
   
   
   private List<Tenant> tenants;
   
   @PostConstruct
   public void init(){
       retrieveAllTenants();
   }
   
    private void retrieveAllTenants() {
        tenants = tenantDAO.findAll();
    }
    
    private Tenant updateAndRefresh(Tenant tenant){
        Tenant mTenant = tenantDAO.makePersistent(tenant);
        retrieveAllTenants();
        return mTenant;
    }
   
   public void openAddTenantDialog(){
        Map<String,Object> options = getDialogOptions(45, 40,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-tenant-dlg", options, null);
   }
   
   public void openEditTenantDialog(@NotNull Tenant tenant){
        Map<String,Object> options = getDialogOptions(45, 40,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-tenant-dlg", options, 
                        getDialogParams(ViewParamKey.TENANT_UUID, 
                                tenant.getUuid().toString()));
   }
   
   public void openAddAdmintDialog(){
        Map<String,Object> options = getDialogOptions(45, 40,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-administrator-dlg", options, null);
   }
   
   
   
    public void openSetAdminDialog(Tenant tenant){
        
        Map<String,Object> options = getDialogOptions(45, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic("choose-admin-dlg", options, 
                        getDialogParams(ViewParamKey.TENANT_UUID,
                                tenant.getUuid().toString()));
        LOG.log(Level.INFO, "-- TENANT UUID:{0}", tenant.getUuid().toString());
    }  
    
    public void disableTenant(Tenant tenant){
        tenant.setStatus(TenantStatus.DISABLED);
        updateAndRefresh(tenant);
        
    }
    
    public void enableTenant(Tenant tenant){
        tenant.setStatus(TenantStatus.ENABLED);
        updateAndRefresh(tenant);
    }
    
    public boolean renderEnableButton(Tenant tenant){
        return tenant.getStatus().equals(TenantStatus.DISABLED);
    }
    
     public boolean renderDisableButton(Tenant tenant){
        return tenant.getStatus().equals(TenantStatus.ENABLED);
    }

   public void handleEditTenantReturn(SelectEvent event){
       LOG.log(Level.INFO, "---> RETURN FROM HANDLE ADD TENZNT...");
       retrieveAllTenants();
       selectedTenant = (Tenant)event.getObject();
   
   }
  
   public void handleSetAdminReturn(SelectEvent event){
       selectedAdminUser = (AdminUser)event.getObject();
       LOG.log(Level.INFO, "--- HANDLE SELECTED ADMIN: {0}", selectedAdminUser);
       
   }
   
   public void updateTenant(Tenant tenant){
       LOG.log(Level.INFO, "--- UPDATE SELECTED ADMIN: {0}", selectedAdminUser);
       if(selectedAdminUser != null){
           tenantService.updateTenantAdmin(tenant, selectedAdminUser);
       }
   }
   
   public String retrieveAdmin(Tenant tenant){
     return (!adminUserDAO.findByTenant(tenant).isEmpty()) 
              ? adminUserDAO.findByTenant(tenant).get(0).getName() : "";
   }
   
   public String retrieveAdminLogin(Tenant tenant){
     return (!adminUserDAO.findByTenant(tenant).isEmpty()) 
              ? adminUserDAO.findByTenant(tenant).get(0).getLogin() : "";
   }
   
   public String retrieveAdminStatus(Tenant tenant){
     if( (!adminUserDAO.findByTenant(tenant).isEmpty()) && 
             (adminUserDAO.findByTenant(tenant).get(0).getStatus() != null) ){
         return adminUserDAO.findByTenant(tenant).get(0).getStatus().getValue();
     }
     return "";      
   }
   
   
   
   public boolean rendererAction(AdminUser adminUser){
        return selectedAdminUsers.contains(adminUser);
    }
    
     
    public void check(AdminUser adminUser){   
       selectedAdminUsers.add(adminUser);
        
    }
    
    public void uncheck(AdminUser adminUser){
       selectedAdminUsers.remove(adminUser);
       
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
