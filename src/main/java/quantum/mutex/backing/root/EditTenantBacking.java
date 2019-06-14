/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.root;

import java.io.Serializable;
import java.util.function.Function;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import org.primefaces.PrimeFaces;
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.dao.TenantDAO;

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
        return Result.of(tenantUUID)
                .flatMap(tenantDAO::findById)
                .getOrElse(() -> new Tenant());
     }
    
    public void processSaveTenant(){
        save.apply(currentTenant).forEach(returnToCaller);
    }
    
    Function<Tenant, Result<Tenant>> save = (@NotNull Tenant t)  
            -> tenantDAO.makePersistent(t);
    
    Effect<Tenant> returnToCaller = (@NotNull Tenant t) 
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
