/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import io.mutex.shared.entity.BaseEntity;
import io.mutex.user.valueobject.UserStatus;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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
   
})
@Table(name = "mx_user")
@Entity
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
      
    private String name;
    
    
    @Email
    @Column(unique = true,length = 100,nullable = false)
    private String login;
    
    @Column(length = 255)
//    @Size(min = 8, max = 255)
    private String password;
    
    @Transient
    private String confirmPassword;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.DISABLED;
    
//    @NotNull
//    @ManyToOne
//    private Group group;
//    
     public User() {
    }
    
   
    public User(String login,String password) {
        this.login = login;
        this.password = password;
//        this.group = group;

    }
    
    public User(User user){
        this.uuid = user.uuid;
        this.version = user.version;
        this.login = user.login;
//        this.space = user.space;
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

//    public Group getGroup() {
//        return group;
//    }
//
//    public void setGroup(Group group) {
//        this.group = group;
//    }
//    
    
   
}
