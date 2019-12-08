/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service.user;

import java.util.Optional;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.repository.RoleDAO;
import io.mutex.repository.UserDAO;
import io.mutex.repository.UserRoleDAO;
import io.mutex.domain.entity.Role;
import io.mutex.domain.valueobject.RoleName;
import io.mutex.domain.entity.User;
import io.mutex.domain.entity.UserRole;


/**
 *
 * @author Florent
 */
@Stateless
public class UserRoleService {
    
    @Inject UserDAO userDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserRoleDAO userRoleDAO;
    
     public Optional<UserRole> persistUserRole(User user, RoleName roleName){
        
        Optional<User> userRes = userDAO.findByLogin(user.getLogin());
        Optional<Role> roleRes = roleDAO.findByName(roleName);

        Optional<UserRole> usr = userRes
                .flatMap(ru -> roleRes.flatMap(rr -> userRoleDAO.findByUserAndRole(ru.getLogin(),rr.getName())));
        
        if(usr.isEmpty()){
            return userRes.flatMap(u -> roleRes.map(r -> {return new UserRole(u, r);}))
                    .flatMap(userRoleDAO::makePersistent)
                    .or(() -> Optional.empty());
        }
        
        return Optional.empty();
     };
    
    @Asynchronous
    public void cleanOrphanLogins(){
        userRoleDAO.findAll().stream()
                .filter(ur -> userNotExist(ur.getUserLogin()))
                .forEach(ur -> userRoleDAO.makeTransient(ur));
    }
    
    private boolean userNotExist(String login){
        return userDAO.findByLogin(login).isEmpty();
    }
}