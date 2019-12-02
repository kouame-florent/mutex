/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import mutex.shared.repository.GenericDAOImpl;
import mutex.user.domain.entity.Group;
import mutex.user.domain.entity.Tenant;
import mutex.user.domain.entity.User;
import mutex.user.domain.entity.UserGroup;


/**
 *
 * @author Florent
 */
@Stateless
public class GroupDAOImpl extends GenericDAOImpl<Group, String> implements GroupDAO{
    
    @Inject UserGroupDAO userGroupDAO;
    
    public GroupDAOImpl() {
        super(Group.class);
    }

    @Override
    public Optional<Group> findByTenantAndName(Tenant tenant, String name) {
        TypedQuery<Group> query = 
               em.createNamedQuery("Group.findByTenantAndName", Group.class);
        query.setParameter("tenant", tenant);
        query.setParameter("name", name);
       
        List<Group> Optionals =  query.getResultList();
        if(!Optionals.isEmpty()){
            return Optional.of(Optionals.get(0));
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

    @Override
    public List<Group> findByUser(User user) {
       return userGroupDAO.findByUser(user).stream()
                .map(UserGroup::getGroup).collect(Collectors.toList());
    }
    
}
