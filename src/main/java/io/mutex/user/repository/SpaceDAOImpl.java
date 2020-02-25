/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.user.entity.Space;
import io.mutex.shared.repository.GenericDAOImpl;


/**
 *
 * @author Florent
 */
@Stateless
public class SpaceDAOImpl extends GenericDAOImpl<Space, String> implements SpaceDAO{
    
    public SpaceDAOImpl() {
        super(Space.class);
    }

    @Override
    public Optional<Space> findByName(String name) {
        
        TypedQuery<Space> query = 
               em.createNamedQuery("Space.findByName", Space.class);
        query.setParameter("name", name);
       
        List<Space> Optionals =  query.getResultList();
        if(!Optionals.isEmpty()){
            return Optional.of(Optionals.get(0));
        }
        
        return Optional.empty();
   }
    
}
