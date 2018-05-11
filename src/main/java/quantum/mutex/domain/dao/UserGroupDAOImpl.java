/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.GroupType;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;

/**
 *
 * @author Florent
 */
@Stateless
public class UserGroupDAOImpl extends GenericDAOImpl<UserGroup, UserGroup.Id> 
        implements UserGroupDAO{
    
    public UserGroupDAOImpl() {
        super(UserGroup.class);
    }

    @Override
    public List<UserGroup> findByUser(User user) {
        TypedQuery<UserGroup> query = 
               em.createNamedQuery("UserGroup.findByUser", UserGroup.class);
        query.setParameter("user", user);
        return  query.getResultList();
        
    }

    @Override
    public List<UserGroup> findByGroup(Group group) {
        TypedQuery<UserGroup> query = 
               em.createNamedQuery("UserGroup.findByGroup", UserGroup.class);
        query.setParameter("group", group);
        return  query.getResultList();
    }

    @Override
    public Optional<UserGroup> findByUserAndGroupType(User user, GroupType groupType) {
        TypedQuery<UserGroup> query = 
               em.createNamedQuery("UserGroup.findByUserAndGroupType", UserGroup.class);
        query.setParameter("user", user);
        query.setParameter("groupType", groupType);  
       
        List<UserGroup> results =  query.getResultList();
        if(!results.isEmpty()){
            return Optional.of(results.get(0));
        }
        
        return Optional.empty();
    }

    @Override
    public long countGroupMembers(Group group) {
        TypedQuery<Long> query = 
               em.createNamedQuery("UserGroup.countGroupMembers", Long.class);
        query.setParameter("group", group);
       
        return query.getSingleResult();
    }
    
}
