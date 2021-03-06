/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import java.util.Optional;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.repository.UserRoleDAO;
import io.mutex.user.entity.Role;
import io.mutex.user.valueobject.RoleName;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;
import java.util.List;


/**
 *
 * @author Florent
 */
@Stateless
public class UserRoleServiceImpl implements UserRoleService {

    @Inject RoleService roleService;
    @Inject UserService userService;
    @Inject UserRoleDAO userRoleDAO;
    
    @Override
     public Optional<UserRole> create(User user, RoleName roleName){
        
        Optional<User> userRes = userService.getByLogin(user.getLogin());
        Optional<Role> roleRes = roleService.getByName(roleName);

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
    @Override
    public void cleanOrphansUserRole(){
        userRoleDAO.findAll().stream()
                .filter(ur -> userNotExist(ur.getUserLogin()))
                .forEach(ur -> userRoleDAO.makeTransient(ur));
    }
    
    private boolean userNotExist(String login){
        return userService.getByLogin(login).isEmpty();
    }
    
    @Override
    public List<UserRole> getByUser(User user){
        return userRoleDAO.findByUser(user);
    }
    
    @Override
    public void delete(UserRole ur){
        userRoleDAO.makeTransient(ur);
    }
}
