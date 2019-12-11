/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import io.mutex.repository.GenericDAOImpl;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.domain.entity.StandardUser;
import io.mutex.domain.entity.Tenant;
import io.mutex.repository.GenericDAOImpl;
import io.mutex.user.entity.User;


/**
 *
 * @author Florent
 */
@Stateless
public class StandardUserDAOImpl extends GenericDAOImpl<StandardUser, String> 
        implements StandardUserDAO{
    
    public StandardUserDAOImpl() {
        super(StandardUser.class);
    }
    
    @Override
    public Optional<User> findByLogin(String login) {
        TypedQuery<User> query = 
               em.createNamedQuery("StandardUser.findByLogin", User.class);
        query.setParameter("login", login);
       
        List<User> Optionals =  query.getResultList();
        if(!Optionals.isEmpty()){
            return Optional.of(Optionals.get(0));
        }
        
        return Optional.empty();
    }

    @Override
    public List<User> findByTenant(Tenant tenant) {
        TypedQuery<User> query = 
               em.createNamedQuery("StandardUser.findByTenant", User.class);
        query.setParameter("tenant", tenant);
       
        return query.getResultList();
    }
}
