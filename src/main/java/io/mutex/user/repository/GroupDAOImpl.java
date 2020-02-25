/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Space;
import io.mutex.shared.repository.GenericDAOImpl;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;


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
    public Optional<Group> findBySpaceAndName(Space space, String name) {
        TypedQuery<Group> query = 
               em.createNamedQuery("Group.findBySpaceAndName", Group.class);
        query.setParameter("space", space);
        query.setParameter("name", name);
       
        List<Group> Optionals =  query.getResultList();
        if(!Optionals.isEmpty()){
            return Optional.of(Optionals.get(0));
        }
        
        return Optional.empty();
    }

    @Override
    public List<Group> findBySpace(Space space) {
        TypedQuery<Group> query = 
               em.createNamedQuery("Group.findBySpace", Group.class);
        query.setParameter("space", space);
       
        return query.getResultList();
    }

    @Override
    public List<Group> findByUser(User user) {
       return userGroupDAO.findByUser(user).stream()
                .map(UserGroup::getGroup).collect(Collectors.toList());
    }
    
}
