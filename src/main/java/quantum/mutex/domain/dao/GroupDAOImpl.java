/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserGroup;

/**
 *
 * @author Florent
 */
@Stateless
public class GroupDAOImpl extends GenericDAOImpl<Group, UUID> implements GroupDAO{
    
    @Inject UserGroupDAO userGroupDAO;
    
    public GroupDAOImpl() {
        super(Group.class);
    }

    @Override
    public Result<Group> findByTenantAndName(Tenant tenant, String name) {
        TypedQuery<Group> query = 
               em.createNamedQuery("Group.findByTenantAndName", Group.class);
        query.setParameter("tenant", tenant);
        query.setParameter("name", name);
       
        List<Group> results =  query.getResultList();
        if(!results.isEmpty()){
            return Result.of(results.get(0));
        }
        
        return Result.empty();
    }

    @Override
    public List<Group> findByTenant(Tenant tenant) {
        TypedQuery<Group> query = 
               em.createNamedQuery("Group.findByTenant", Group.class);
        query.setParameter("tenant", tenant);
       
        return query.getResultList();
    }

    @Override
    public List<Group> findByUser(User user) {
       return userGroupDAO.findByUser(user).stream()
                .map(UserGroup::getGroup).collect(Collectors.toList());
    }
    
}
