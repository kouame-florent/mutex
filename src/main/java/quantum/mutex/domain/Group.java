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
@Table(name = "group")
@Entity
public class Group extends BusinessEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;

    public Group() {
    }
    
        
    public Group(Tenant tenant, String name) {
        this.tenant = tenant;
        this.name = name;
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
    
    
    
}
