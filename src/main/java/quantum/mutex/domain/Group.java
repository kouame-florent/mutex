/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "Group.findByTenantAndName",
        query = "SELECT g FROM Group g WHERE g.tenant = :tenant AND g.name = :name"
    ),
    @NamedQuery(
        name = "Group.findByTenant",
        query = "SELECT g FROM Group g WHERE g.tenant = :tenant"
    ),
})
@Table(name = "mx_group")
@Entity
public class Group extends BusinessEntity implements Serializable {

    private static final long serialVersionUID = 1L;
  
    @Size(max = 50)
    private String name;
    
    @Size(max = 255)
    private String description;
    
    @Transient
    private boolean primary;
    
    public Group() {
    }
    
        
    public Group(Tenant tenant, String name) {
        this.tenant = tenant;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Group setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public Group setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

 
    
    
}
