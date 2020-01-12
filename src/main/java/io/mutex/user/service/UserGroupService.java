/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.UserGroupDAO;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;
import io.mutex.user.valueobject.GroupType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Florent
 */
@Stateless
public class UserGroupService {
    
    @Inject UserGroupDAO userGroupDAO;
    @Inject GroupDAO groupDAO;
    
    
    public List<UserGroup> findByGroup(@NotNull Group group){
        return userGroupDAO.findByGroup(group);
    }
    
    public List<UserGroup> findByUser(@NotNull User user){
        return userGroupDAO.findByUser(user);
    }
    
    public Optional<UserGroup> findUserPrimaryGroup(@NotNull StandardUser user){
        return userGroupDAO.findUserPrimaryGroup(user);
    }
    
    public List<UserGroup> findByUserAndGroupType(@NotNull StandardUser user,@NotNull GroupType groupType){
        return userGroupDAO.findByUserAndGroupType(user, groupType);
    }
    
    
    public long countAssociations(@NotNull User user){
        return userGroupDAO.countAssociations(user);
    }
    
    public long countGroupMembers(@NotNull Group group){
      return userGroupDAO.countGroupMembers(group);
    }
    
    public List<Group> getAllGroups(@NotNull User user){
        return userGroupDAO.findByUser(user).stream()
                    .map(ug -> groupDAO.findById(ug.getGroup().getUuid()))
                    .flatMap(Optional::stream)
                    .collect(toList());
   }
    
    public List<Group> getGroups(@NotNull User user){
        return userGroupDAO.findByUser(user)
                .stream().map(UserGroup::getGroup)
                .collect(Collectors.toList());
    }
    
     public void associateGroups(List<Group> groups,@NotNull StandardUser user){
        createPrimaryUsersGroups(groups,user);
        createSecondaryUsersGroups(groups,user);
        removeUnselectedUsersGroups(groups,user);
       
    }
    
    public void createPrimaryUsersGroups(List<Group> groups,StandardUser user){
        groups.stream()
                .filter(Group::isEdited)
                .filter(Group::isPrimary)
                .map(g -> editUserGroup(user,g,GroupType.PRIMARY))
                .forEach(userGroupDAO::makePersistent);
    }
    
     private void createSecondaryUsersGroups(List<Group> groups,@NotNull StandardUser user){
        groups.stream()
                .filter(Group::isEdited)
                .filter(g -> !g.isPrimary())
                .map(g -> editUserGroup(user,g,GroupType.SECONDARY))
                .forEach(userGroupDAO::makePersistent);
   }
   
    private UserGroup editUserGroup(@NotNull StandardUser user,@NotNull Group group,@NotNull GroupType type){
        Optional<UserGroup> oUg = userGroupDAO.findByUserAndGroup(user, group);
        return oUg.map(ug -> {ug.setGroupType(type);return ug;} )
                .orElseGet(() -> new UserGroup(user, group, type));
        
    }
    
    private void removeUnselectedUsersGroups(List<Group> groups,@NotNull StandardUser user){
        groups.stream().filter(g -> !g.isEdited())
            .map(g -> userGroupDAO.findByUserAndGroup(user, g))
            .forEach(rug -> rug.map(userGroupDAO::makeTransient));
    }
    
    public void remove(@NotNull UserGroup ug){
        userGroupDAO.makeTransient(ug);
    }
 

}
