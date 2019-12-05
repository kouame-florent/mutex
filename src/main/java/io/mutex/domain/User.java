/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.domain;

import mutex.user.domain.valueobject.UserStatus;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;






/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "User.findByLogin",
        query = "SELECT u FROM User u WHERE u.login = :login"
    ),
    @NamedQuery(
        name = "User.findByLoginAndPassword",
        query = "SELECT u FROM User u WHERE u.login = :login AND u.password = :password "
    ),
//    @NamedQuery(
//        name = "User.findEnabled",
//        query = "SELECT u FROM User u WHERE u.status = ENABLED "
//    ),
   @NamedQuery(
        name = "User.findByTenant",
        query = "SELECT u FROM User u WHERE u.tenant = :tenant"
    ),
})
@Table(name = "mx_user")
@Entity
public class User extends BusinessEntity implements Serializable {

    private static final long serialVersionUID = 1L;
      
    private String name;
    
    
    @Email
   // @NotNull
    @Column(unique = true,length = 100,nullable = false)
    private String login;
    
    @Column(length = 255)
//    @Size(min = 8, max = 255)
    private String password;
    
    @Transient
    private String confirmPassword;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.DISABLED;
    
     public User() {
    }
    
   
    public User(String login,Tenant tenant) {
        this.login = login;
        this.tenant = tenant;
    }
    
    public User(User user){
        this.uuid = user.uuid;
        this.version = user.version;
        this.login = user.login;
        this.tenant = user.tenant;
        this.password = user.password;
        this.status = user.status;
    }
  
   
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    
   
}
