/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.GroupType;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;

/**
 *
 * @author Florent
 */
public interface UserGroupDAO extends GenericDAO<UserGroup, UserGroup.Id>{
    
    List<UserGroup> findByUser(User user);
    List<UserGroup> findByGroup(Group group);
    Result<UserGroup> findByUserAndGroup(User user,Group group);
    Result<UserGroup> findUserPrimaryGroup(User user);
    List<UserGroup> findByUserAndGroupType(User user,GroupType groupType);
    long countGroupMembers(Group group);
    long countAssociations(User user);
}
