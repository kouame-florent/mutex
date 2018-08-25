/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.mutex.domain.StandardUser;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.User;

/**
 *
 * @author Florent
 */
@Stateless
public class StandardUserDAOImpl extends GenericDAOImpl<StandardUser, UUID> 
        implements StandardUserDAO{
    
    public StandardUserDAOImpl() {
        super(StandardUser.class);
    }
    
    @Override
    public Optional<User> findByLogin(String login) {
        TypedQuery<User> query = 
               em.createNamedQuery("StandardUser.findByLogin", User.class);
        query.setParameter("login", login);
       
        List<User> results =  query.getResultList();
        if(!results.isEmpty()){
            return Optional.of(results.get(0));
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