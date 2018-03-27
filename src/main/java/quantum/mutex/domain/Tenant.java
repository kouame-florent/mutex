/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import javax.persistence.Column;
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
        name = "Tenant.findByName",
        query = "SELECT t FROM Tenant t WHERE t.name = :name"
    ),
   
})
@Table(name = "tenant")
@Entity
public class Tenant extends BaseEntity{
    
    
    @Column(unique = true)
    private String name;

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
    
    
}
