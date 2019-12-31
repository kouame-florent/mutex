/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;


import io.mutex.shared.entity.BaseEntity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;



/**
 *
 * @author Florent
 */

@MappedSuperclass
public abstract class BusinessEntity extends BaseEntity{
      
    private static final long serialVersionUID = 1L;
	
    @JoinColumn(name = "tenant_uuid")
    @ManyToOne
    protected Tenant tenant;

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    
    
}
