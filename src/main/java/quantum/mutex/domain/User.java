/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

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
        name = "User.findByTenant",
        query = "SELECT u FROM User u WHERE u.tenant = :tenant"
    ),
})
@Table(name = "user")
@Entity
public class User extends BusinessEntity implements Serializable {

    private static final long serialVersionUID = 1L;
      
    private String name;
    
    @Email
    @NotNull
    @Column(unique = true)
    private String login;
    
    private String password;
    
    @Transient
    private String confirmPassword;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
     public User() {
    }
    
   
    public User(String login,Tenant tenant) {
        this.login = login;
        this.tenant = tenant;
        
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
