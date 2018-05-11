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
import quantum.mutex.domain.Tenant;

/**
 *
 * @author Florent
 */
@Stateless
public class GroupDAOImpl extends GenericDAOImpl<Group, UUID> implements GroupDAO{
    
    public GroupDAOImpl() {
        super(Group.class);
    }

    @Override
    public Optional<Group> findByTenantAndName(Tenant tenant, String name) {
        TypedQuery<Group> query = 
               em.createNamedQuery("Group.findByTenantAndName", Group.class);
        query.setParameter("tenant", tenant);
        query.setParameter("name", name);
       
        List<Group> results =  query.getResultList();
        if(!results.isEmpty()){
            return Optional.of(results.get(0));
        }
        
        return Optional.empty();
    }

    @Override
    public List<Group> findByTenant(Tenant tenant) {
        TypedQuery<Group> query = 
               em.createNamedQuery("Group.findByTenant", Group.class);
        query.setParameter("tenant", tenant);
       
        return query.getResultList();
    }
    
}
