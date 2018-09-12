/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.mutex.domain.Role;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserRole;

/**
 *
 * @author Florent
 */
@Stateless
public class UserRoleDAOImpl extends GenericDAOImpl<UserRole, UserRole.Id> 
        implements UserRoleDAO{
    
    public UserRoleDAOImpl() {
        super(UserRole.class);
    }

    @Override
    public List<UserRole> findByUser(User user) {
        TypedQuery<UserRole> query = 
               em.createNamedQuery("UserRole.findByUser", UserRole.class);
        query.setParameter("user", user);
        return  query.getResultList();
    }

    @Override
    public List<UserRole> findByRole(Role role) {
        TypedQuery<UserRole> query = 
               em.createNamedQuery("UserRole.findByRole", UserRole.class);
        query.setParameter("role", role);
        return  query.getResultList();
    }

    @Override
    public Optional<UserRole> findByUserAndRole(User user, Role role) {
         TypedQuery<UserRole> query = 
               em.createNamedQuery("UserRole.findByUserAndRole", UserRole.class);
        query.setParameter("user", user);
        query.setParameter("role", role);  
        
       return query.getResultList().isEmpty()? Optional.empty() 
                : Optional.ofNullable(query.getResultList().get(0));
    }
    
}
