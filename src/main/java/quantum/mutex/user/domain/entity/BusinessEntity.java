/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.domain.entity;


import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.shared.domain.entity.BaseEntity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;



/**
 *
 * @author Florent
 */

@MappedSuperclass
public abstract class BusinessEntity extends BaseEntity{
    
    @ManyToOne
    protected Tenant tenant;

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    
    
}
