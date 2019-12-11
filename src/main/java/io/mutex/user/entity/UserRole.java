/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.domain.entity.BaseEntity;
import io.mutex.domain.entity.Role;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
        name = "UserRole.findByUser",
        query = "SELECT ur FROM UserRole ur WHERE ur.userLogin = :userLogin"
    ),
    @NamedQuery(
        name = "UserRole.findByRole",
        query = "SELECT ur FROM UserRole ur WHERE ur.roleName = :roleName"
    ),
    @NamedQuery(
        name = "UserRole.findByUserAndRole",
        query = "SELECT ur FROM UserRole ur WHERE ur.userLogin = :userLogin AND ur.roleName = :roleName"
    ),
    
})
@Table(name = "mx_user_role",uniqueConstraints =
            @UniqueConstraint(
                name = "UNQ_ROLE_LOGIN",
                columnNames = { "role_name", "login"})
)
@Entity
public class UserRole extends BaseEntity implements Serializable {
   
    
    @Column(name = "role_name",length = 100)
    private String roleName;
    
    @Column(name = "login",length = 100)
    private String userLogin;

    public UserRole() {
    }
    

    public UserRole(User user,Role role) {
       this.roleName = role.getName();
        this.userLogin = user.getLogin();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
    
    
}
