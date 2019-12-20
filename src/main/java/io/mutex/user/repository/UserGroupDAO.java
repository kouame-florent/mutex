/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.Group;
import io.mutex.user.valueobject.GroupType;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;


/**
 *
 * @author Florent
 */
public interface UserGroupDAO extends GenericDAO<UserGroup, UserGroup.Id>{
    
    List<UserGroup> findByUser(User user);
    List<UserGroup> findByGroup(Group group);
    Optional<UserGroup> findByUserAndGroup(User user,Group group);
    Optional<UserGroup> findUserPrimaryGroup(User user);
    List<UserGroup> findByUserAndGroupType(User user,GroupType groupType);
    long countGroupMembers(Group group);
    long countAssociations(User user);
}
