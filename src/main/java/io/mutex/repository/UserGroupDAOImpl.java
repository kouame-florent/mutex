/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.repository;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.repository.GenericDAOImpl;
import io.mutex.domain.entity.Group;
import io.mutex.domain.valueobject.GroupType;
import io.mutex.domain.entity.User;
import io.mutex.domain.entity.UserGroup;


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
    public Optional<UserGroup> findUserPrimaryGroup(User user) {
        TypedQuery<UserGroup> query = 
               em.createNamedQuery("UserGroup.findByUserAndGroupType", UserGroup.class);
        query.setParameter("user", user);
        query.setParameter("groupType", GroupType.PRIMARY);  
       
        return query.getResultList().isEmpty() ? Optional.empty() : 
                Optional.ofNullable(query.getResultList().get(0));

    }
    
    @Override
    public List<UserGroup> findByUserAndGroupType(User user, GroupType groupType) {
         TypedQuery<UserGroup> query = 
               em.createNamedQuery("UserGroup.findByUserAndGroupType", UserGroup.class);
        query.setParameter("user", user);
        query.setParameter("groupType", groupType);  
       
        return query.getResultList();
    }


    @Override
    public Optional<UserGroup> findByUserAndGroup(User user, Group group) {
         TypedQuery<UserGroup> query = 
               em.createNamedQuery("UserGroup.findByUserAndGroup", UserGroup.class);
        query.setParameter("user", user);
        query.setParameter("group", group);  
        
       return query.getResultList().isEmpty()? Optional.empty() 
                : Optional.of(query.getResultList().get(0));
    }
    
    
    @Override
    public long countGroupMembers(Group group) {
        TypedQuery<Long> query = 
               em.createNamedQuery("UserGroup.countGroupMembers", Long.class);
        query.setParameter("group", group);
       
        return query.getSingleResult();
    }

    @Override
    public long countAssociations(User user) {
        TypedQuery<Long> query = 
               em.createNamedQuery("UserGroup.countAssociations", Long.class);
        query.setParameter("user", user);
       
        return query.getSingleResult();
    }

   
    
}
