/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.shared.entity.BaseEntity;
import java.util.Locale;
import io.mutex.user.valueobject.SpaceStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Florents
 */
@NamedQueries({
    @NamedQuery(
        name = "Space.findByName",
        query = "SELECT t FROM Space t WHERE t.name = :name"
    ),
   
})
@Table(name = "mx_space")
@Entity
public class Space extends BaseEntity implements Nameable{
       
    private static final long serialVersionUID = 1L;
    
    @NotNull
    @Column(unique = true,length = 50)
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$",message = "Les caratères spéciaux ne sont pas autorisés")
    private String name;
    
    @Size(max = 255)
    private String description;

    
    @Enumerated(EnumType.STRING)
    private SpaceStatus status = SpaceStatus.ENABLED;

    public Space(String name, String description) {
        this.name = name.toUpperCase(Locale.getDefault());
        this.description = description;
    }
    
    public Space(String name) {
        this.name = name.toUpperCase(Locale.getDefault());
    }
    
    public Space() {
    }
    
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SpaceStatus getStatus() {
        return status;
    }

    public void setStatus(SpaceStatus status) {
        this.status = status;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
