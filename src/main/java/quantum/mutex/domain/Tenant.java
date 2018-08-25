/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "Tenant.findByName",
        query = "SELECT t FROM Tenant t WHERE t.name = :name"
    ),
   
})
@Table(name = "mx_tenant")
@Entity
public class Tenant extends BaseEntity{
    
    @Column(unique = true)
    @Size(max = 50)
    private String name;
    
    
    @Size(max = 255)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TenantStatus status = TenantStatus.ENABLED;

    public Tenant(String name) {
        this.name = name;
    }
    
    public Tenant() {
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }
    
    
}
