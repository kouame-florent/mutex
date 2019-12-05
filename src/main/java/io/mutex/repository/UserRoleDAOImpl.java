/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.repository;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.repository.GenericDAOImpl;
import io.mutex.domain.entity.Role;
import io.mutex.domain.entity.User;
import io.mutex.domain.entity.UserRole;


/**
 *
 * @author Florent
 */
@Stateless
public class UserRoleDAOImpl extends GenericDAOImpl<UserRole, String> 
        implements UserRoleDAO{
    
    public UserRoleDAOImpl() {
        super(UserRole.class);
    }

    @Override
    public List<UserRole> findByUser(User user) {
        TypedQuery<UserRole> query = 
               em.createNamedQuery("UserRole.findByUser", UserRole.class);
        query.setParameter("userLogin", user.getLogin());
        return  query.getResultList();
    }

    @Override
    public List<UserRole> findByRole(Role role) {
        TypedQuery<UserRole> query = 
               em.createNamedQuery("UserRole.findByRole", UserRole.class);
        query.setParameter("roleName", role.getName());
        return  query.getResultList();
    }

    @Override
    public Optional<UserRole> findByUserAndRole(String userLogin, String roleName) {
         TypedQuery<UserRole> query = 
               em.createNamedQuery("UserRole.findByUserAndRole", UserRole.class);
        query.setParameter("userLogin", userLogin);
        query.setParameter("roleName", roleName);  
        
       return query.getResultList().isEmpty()? Optional.empty() 
                : Optional.of(query.getResultList().get(0));
    }
    
}
