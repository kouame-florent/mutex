/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.dao.TenantDAO;

/**
 *
 * @author Florent
 */
@Named(value = "tenantBacking")
@ViewScoped
public class TenantBacking extends BaseBacking implements Serializable{
    
   private static final Logger LOG = Logger.getLogger(TenantBacking.class.getName());
   
   @Inject TenantDAO tenantDAO;
   private Tenant selectedTenant;
   
   private final List<Tenant> tenants = new ArrayList<>();
   
   @PostConstruct
   public void init(){
       initTenants();
   }
   
   private void initTenants(){
       selectedTenant = null;
       tenants.clear();
       tenants.addAll(retrieveAllTenants());
   }
   
   public void openAddTanantDialog(){
        Map<String,Object> options = getDialogOptions(45, 40);
        PrimeFaces.current().dialog()
                .openDynamic("edit-tenant-dlg", options, null);
   }

   public void handleAddTenantReturn(SelectEvent event){
       LOG.log(Level.INFO, "---> RETURN FROM HANDLE ADD TENZNT...");
       initTenants();
       selectedTenant = (Tenant)event.getObject();
   
   }
   
    private List<Tenant> retrieveAllTenants() {
        return tenantDAO.findAll();
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
   
   
    
}
