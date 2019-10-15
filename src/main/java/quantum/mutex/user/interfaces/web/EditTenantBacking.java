/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.interfaces.web;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import quantum.mutex.shared.interfaces.web.BaseBacking;
import quantum.mutex.shared.interfaces.web.ViewParamKey;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.repository.TenantDAO;

/**
 *
 * @author Florent
 */
@Named(value = "editTenantBacking")
@ViewScoped
public class EditTenantBacking extends BaseBacking implements Serializable{
    
    private final ViewParamKey tenantParamKey = ViewParamKey.TENANT_UUID;
    
    private String tenantUUID;
    
    @Inject
    private TenantDAO tenantDAO;
    
    private Tenant currentTenant;
    
    public void viewAction(){
        currentTenant = initTenant(tenantUUID);
    }
    
    private Tenant initTenant(String tenantUUID){
        return Optional.of(tenantUUID)
                .flatMap(tenantDAO::findById)
                .orElseGet(() -> new Tenant());
     }
    
    public void processSaveTenant(){
        save.apply(currentTenant).ifPresent(returnToCaller);
    }
    
    Function<Tenant,Optional<Tenant>> save = ( Tenant t)  
            -> tenantDAO.makePersistent(t);
    
    Consumer<Tenant> returnToCaller = ( Tenant t) 
            -> PrimeFaces.current().dialog().closeDynamic(t);

    public String getTenantUUID() {
        return tenantUUID;
    }

    public void setTenantUUID(String tenantUUID) {
        this.tenantUUID = tenantUUID;
    }

    public ViewParamKey getTenantParamKey() {
        return tenantParamKey;
    }

    public Tenant getCurrentTenant() {
        return currentTenant;
    }

    public void setCurrentTenant(Tenant currentTenant) {
        this.currentTenant = currentTenant;
    }

}
