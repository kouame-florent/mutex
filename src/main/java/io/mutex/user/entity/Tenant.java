/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.shared.entity.BaseEntity;
import java.util.Locale;
import io.mutex.user.valueobject.TenantStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

/**
 *
 * @author Florents
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
       
    private static final long serialVersionUID = 1L;
    
    @Column(unique = true,length = 50)
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$",message = "Les caratères spéciaux ne sont pas autorisés")
    private String name;
    
//    @Size(max = 255)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TenantStatus status = TenantStatus.ENABLED;

    public Tenant(String name, String description) {
        this.name = name.toUpperCase(Locale.getDefault());
        this.description = description;
    }
    
    public Tenant(String name) {
        this.name = name.toUpperCase(Locale.getDefault());
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
