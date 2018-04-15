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
import quantum.mutex.domain.Group;
import quantum.mutex.domain.Role;

/**
 *
 * @author Florent
 */
@Stateless
public class RoleDAOImpl extends GenericDAOImpl<Role, UUID> implements RoleDAO{
    
    public RoleDAOImpl() {
        super(Role.class);
    }

    @Override
    public Optional<Role> findByName(String name) {
        TypedQuery<Role> query = 
               em.createNamedQuery("Role.findByName", Role.class);
        query.setParameter("name", name);
       
        List<Role> results =  query.getResultList();
        if(!results.isEmpty()){
            return Optional.of(results.get(0));
        }
        
        return Optional.empty();
    }
    
}
