/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

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
    
    /**
     *
     */
//    @Embeddable
//    public static class Id implements Serializable{
//        
//        @Column(name = "login")
//        private String login;
//
//        @Column(name = "role_name")
//        private String roleName;
//         
//        public Id(){}
//         
//        public Id(User user, Role role){
//             this.login = user.getLogin();
//             this.roleName = role.getName();
//         }
//
//        public String getLogin() {
//            return login;
//        }
//
//        public void setLogin(String login) {
//            this.login = login;
//        }
//
//        public String getRoleName() {
//            return roleName;
//        }
//
//        public void setRoleName(String roleName) {
//            this.roleName = roleName;
//        }
//
//        @Override
//        public int hashCode() {
//            int hash = 5;
//            hash = 17 * hash + Objects.hashCode(this.login);
//            hash = 17 * hash + Objects.hashCode(this.roleName);
//            return hash;
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            }
//            if (obj == null) {
//                return false;
//            }
//            if (getClass() != obj.getClass()) {
//                return false;
//            }
//            final Id other = (Id) obj;
//            if (!Objects.equals(this.login, other.login)) {
//                return false;
//            }
//            return Objects.equals(this.roleName, other.roleName);
//        }
//          
//    }
    
    
//    @EmbeddedId
//    protected Id id = new Id();
    
//    @Version
//    protected long version;
//    
//    @ManyToOne
//    @JoinColumn(name = "role_name",insertable = false,updatable = false,referencedColumnName = "name")
//    private Role role;
    
//    @ManyToOne
////    @JoinColumn(name = "login",insertable = false,updatable = false,referencedColumnName = "login")
//    private User user;
    
    @Column(name = "role_name")
    private String roleName;
    
    @Column(name = "login")
    private String userLogin;

    public UserRole() {
    }
    

    public UserRole(User user,Role role) {
        
//        this.id = new Id(user, role);
        
        this.roleName = role.getName();
        this.userLogin = user.getLogin();
    }

//    public Id getId() {
//        return id;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public User getUser() {
//        return user;
//    }

//    public long getVersion() {
//        return version;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 67 * hash + Objects.hashCode(this.id);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final UserRole other = (UserRole) obj;
//        if (!Objects.equals(this.id, other.id)) {
//            return false;
//        }
//        return true;
//    }
//    
//    

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