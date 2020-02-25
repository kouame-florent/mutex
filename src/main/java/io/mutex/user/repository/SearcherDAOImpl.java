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
import io.mutex.user.entity.Searcher;
import io.mutex.user.entity.Space;
import io.mutex.shared.repository.GenericDAOImpl;
import io.mutex.user.entity.User;


/**
 *
 * @author Florent
 */
@Stateless
public class SearcherDAOImpl extends GenericDAOImpl<Searcher, String> 
        implements SearcherDAO{
    
    public SearcherDAOImpl() {
        super(Searcher.class);
    }
    
    @Override
    public Optional<Searcher> findByLogin(String login) {
        TypedQuery<Searcher> query = 
               em.createNamedQuery("StandardUser.findByLogin", Searcher.class);
        query.setParameter("login", login);
        return query.getResultStream().findFirst();
       
    }

    @Override
    public List<Searcher> findByTenant(Space tenant) {
        TypedQuery<Searcher> query = 
               em.createNamedQuery("StandardUser.findByTenant", Searcher.class);
        query.setParameter("tenant", tenant);
       
        return query.getResultList();
    }
}
