/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.shared.entity.BaseEntity;
import io.mutex.user.valueobject.GroupType;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


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
@Table(name = "mx_user_group",uniqueConstraints =
	@UniqueConstraint(
		    name = "UNQ_USER_GROUP",
		    columnNames = { "user_uuid", "group_uuid"})
)
@Entity
public class UserGroup extends BaseEntity implements Serializable{
       
    private static final long serialVersionUID = 7369035538082793766L;
    
    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "group_uuid")
    private Group group;
       
    public User getUser() {
            return user;
    }

//	public void setUser(StandardUser user) {
//		this.user = user;
//	}
//
//	public long getVersion() {
//		return version;
//	}
//
//	@Version
//    protected long version;
           
    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    public UserGroup() {
    }
    
    public UserGroup(User user, Group group,GroupType groupType) {
    
        this.user = user;
        this.group = group;
        this.groupType = groupType;
    }
    
    public UserGroup(UserGroup userGroup){
        this.user =  userGroup.user;
        this.group =  userGroup.group;
        this.groupType =  userGroup.groupType;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }


    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }
   
}
