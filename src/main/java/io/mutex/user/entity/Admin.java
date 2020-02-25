/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;




/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "Admin.findByLogin",
        query = "SELECT a FROM Admin a WHERE a.login = :login"
    ),
   @NamedQuery(
        name = "Admin.findBySpace",
        query = "SELECT a FROM Admin a WHERE a.group.space = :space"
    ),
//   @NamedQuery(
//        name = "Admin.findNotAssignedToSpace",
//        query = "SELECT a FROM Admin a WHERE a.group.space IS NULL"
//    ),
})
@Entity
public class Admin extends User{
  
    private static final long serialVersionUID = 1L;

    public Admin() {
    }

    public Admin(String login, String password,Group group) {
        super(login, password,group);
    }
    
    
    
}
