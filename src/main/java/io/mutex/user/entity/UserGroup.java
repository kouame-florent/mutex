/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.search.valueobject.GroupType;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "UserGroup.findByUser",
        query = "SELECT ug FROM UserGroup ug WHERE ug.user = :user"
    ),
    @NamedQuery(
        name = "UserGroup.findByGroup",
        query = "SELECT ug FROM UserGroup ug WHERE ug.group = :group"
    ),
    @NamedQuery(
        name = "UserGroup.findByUserAndGroup",
        query = "SELECT ug FROM UserGroup ug WHERE ug.user = :user AND ug.group = :group"
    ),
    @NamedQuery(
        name = "UserGroup.findByUserAndGroupType",
        query = "SELECT ug FROM UserGroup ug WHERE ug.user = :user AND ug.groupType = :groupType"
    ),
    @NamedQuery(
        name = "UserGroup.countGroupMembers",
        query = "SELECT COUNT(ug) FROM UserGroup ug WHERE ug.group = :group"
    ),
    @NamedQuery(
        name = "UserGroup.countAssociations",
        query = "SELECT COUNT(ug) FROM UserGroup ug WHERE ug.user = :user"
    ),
   
})
@Table(name = "mx_user_group")
@Entity
public class UserGroup implements Serializable{
    
    /**
    * 
    */
    private static final long serialVersionUID = 7369035538082793766L;
    @Embeddable
    public static class Id implements Serializable{
        
        private static final long serialVersionUID = 1L; 
        
        @Getter
        @Column(name = "user_uuid",length = 100)
        private String userId;
        
        @Getter
        @Column(name = "group_uuid",length = 100)
        private String groupId;
         
        public Id(){}
         
        public Id(User user, Group group){
             this.userId = user.getUuid();
             this.groupId = group.getUuid();
         }

        

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + Objects.hashCode(this.userId);
            hash = 29 * hash + Objects.hashCode(this.groupId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Id other = (Id) obj;
            if (!Objects.equals(this.userId, other.userId)) {
                return false;
            }
            if (!Objects.equals(this.groupId, other.groupId)) {
                return false;
            }
            return true;
        }

    }
    
    
    @EmbeddedId
    protected Id id = new Id();
    
    @Version
    protected long version;
    
    @ManyToOne
    @JoinColumn(name = "user_uuid",insertable = false,updatable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "group_uuid",insertable = false,updatable = false)
    private Group group;
    
    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    public UserGroup() {
    }
    
    public UserGroup(User user, Group group,GroupType groupType) {
        this.id = new Id(user, group);
        this.user = user;
        this.group = group;
        this.groupType = groupType;
    }
    
    public UserGroup(UserGroup userGroup){
        this.id = userGroup.id;
//        this.version = user.version;
        this.user =  userGroup.user;
        this.group =  userGroup.group;
        this.groupType =  userGroup.groupType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Id getId() {
        return id;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.id);
        hash = 61 * hash + Objects.hashCode(this.user);
        hash = 61 * hash + Objects.hashCode(this.group);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserGroup other = (UserGroup) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.group, other.group)) {
            return false;
        }
        return true;
    }
   
    
    
}
