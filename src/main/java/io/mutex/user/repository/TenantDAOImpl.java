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
import io.mutex.domain.entity.Tenant;
import io.mutex.repository.GenericDAOImpl;


/**
 *
 * @author Florent
 */
@Stateless
public class TenantDAOImpl extends GenericDAOImpl<Tenant, String> implements TenantDAO{
    
    public TenantDAOImpl() {
        super(Tenant.class);
    }

    @Override
    public Optional<Tenant> findByName(String name) {
        
        TypedQuery<Tenant> query = 
               em.createNamedQuery("Tenant.findByName", Tenant.class);
        query.setParameter("name", name);
       
        List<Tenant> Optionals =  query.getResultList();
        if(!Optionals.isEmpty()){
            return Optional.of(Optionals.get(0));
        }
        
        return Optional.empty();
   }
    
}
