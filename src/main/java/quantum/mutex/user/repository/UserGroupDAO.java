/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import quantum.mutex.shared.repository.GenericDAO;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.valueobject.GroupType;
import quantum.mutex.user.domain.entity.User;
import quantum.mutex.user.domain.entity.UserGroup;


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
