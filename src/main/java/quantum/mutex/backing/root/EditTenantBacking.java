/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.root;

import java.io.Serializable;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.dao.TenantDAO;

/**
 *
 * @author Florent
 */
@Named(value = "editTenantBacking")
@RequestScoped
public class EditTenantBacking extends BaseBacking implements Serializable{
    
    
    private final ViewParamKey tenantParamKey = ViewParamKey.TENANT_UUID;
    private String tenantUUID;
    private ViewState viewState = ViewState.CREATE;
    
    @Inject
    private TenantDAO tenantDAO;
    
    private Tenant currentTenant;
    
    @PostConstruct
    public void init(){
        currentTenant = new Tenant();
    }
    
    
    public void viewAction(){
        if(!StringUtils.isBlank(tenantUUID)){
            viewState = ViewState.UPDATE;
            currentTenant = tenantDAO.findById(UUID.fromString(tenantUUID));
        }
    
    }
    
    public void persist(){
        Tenant persistentTenant = tenantDAO.makePersistent(currentTenant);
        PrimeFaces.current().dialog().closeDynamic(persistentTenant);
    }
    
    public void close(){
        PrimeFaces.current().dialog().closeDynamic(null);
    }

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
