/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.repository;

import io.mutex.repository.GenericDAOImpl;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.domain.entity.Role;
import io.mutex.domain.valueobject.RoleName;


/**
 *
 * @author Florent
 */
@Stateless
public class RoleDAOImpl extends GenericDAOImpl<Role, String> implements RoleDAO{
    
    public RoleDAOImpl() {
        super(Role.class);
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        TypedQuery<Role> query = 
               em.createNamedQuery("Role.findByName", Role.class);
        query.setParameter("name", name.value());
       
        List<Role> Optionals =  query.getResultList();
        if(!Optionals.isEmpty()){
            return Optional.of(Optionals.get(0));
        }
        
        return Optional.empty();
    }
    
}
