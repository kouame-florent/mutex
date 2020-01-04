/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import io.mutex.shared.repository.GenericDAOImpl;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.entity.Tenant;
import io.mutex.shared.repository.GenericDAOImpl;
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
    public Optional<StandardUser> findByLogin(String login) {
        TypedQuery<StandardUser> query = 
               em.createNamedQuery("StandardUser.findByLogin", StandardUser.class);
        query.setParameter("login", login);
        return query.getResultStream().findFirst();
       
    }

    @Override
    public List<StandardUser> findByTenant(Tenant tenant) {
        TypedQuery<StandardUser> query = 
               em.createNamedQuery("StandardUser.findByTenant", StandardUser.class);
        query.setParameter("tenant", tenant);
       
        return query.getResultList();
    }
}
