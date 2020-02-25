/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.shared.entity.BaseEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "Group.findBySpaceAndName",
        query = "SELECT g FROM Group g WHERE g.space = :space AND g.name = :name"
    ),
    @NamedQuery(
        name = "Group.findBySpace",
        query = "SELECT g FROM Group g WHERE g.space = :space"
    ),
})
@Table(name = "mx_group")

@Entity
public class Group extends BaseEntity implements Nameable, Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(length = 50)
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$",message = "Le nom de groupe ne contenir ni accents ni caractères spéciaux.")
    private String name;
    
    //@Size(max = 255)
    // @Column(length = 255)
    private String description;
    
    @Transient
    private boolean primary;
    
    @ManyToOne
    private Space space;
    
    public Group() {
    }
    
        
    public Group(String name,Space space) {
        this.space = space;
        this.name = name;
    }
    
    public Group(Group group){
        this.space = group.space;
        this.name = group.name;
        this.description = group.description;
        this.primary = group.primary;
        this.edited = group.edited;
    }

    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
       
    }

    public boolean isPrimary() {
        return primary;
    }

    public Group setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }
    
    
    

}
