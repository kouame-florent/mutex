/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.shared.entity.BaseEntity;
import io.mutex.user.valueobject.RoleName;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@Table(name = "mx_role")
@Entity
public class Role extends BaseEntity{
    
//    @NotNull
    @Column(unique = true,length = 50,nullable = false)
    private String name;

    public Role() {
    }
    
    public Role(RoleName name) {
        this.name = name.name();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
