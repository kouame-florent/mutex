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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "Role.findByName",
        query = "SELECT r FROM Role r WHERE r.name = :name"
    ),
   
})
@Entity
public class Role extends BaseEntity{
    
    @NotNull
    @Column(unique = true)
    private String name;

    public Role() {
    }
    
    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
