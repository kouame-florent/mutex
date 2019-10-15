/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.repository;

import quantum.mutex.shared.repository.GenericDAOImpl;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.mutex.user.domain.entity.StandardUser;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.domain.entity.User;


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
