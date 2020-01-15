/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.entity.Group;
import io.mutex.user.valueobject.GroupType;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.UserGroupDAO;
import io.mutex.index.service.FileIOService;
import io.mutex.index.service.ManageIndicesService;
import io.mutex.shared.event.GroupCreated;
import io.mutex.shared.event.GroupDeleted;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
import io.mutex.user.entity.Tenant;
import io.mutex.user.exception.GroupNameExistException;
import io.mutex.user.repository.UserDAO;
import io.mutex.user.valueobject.UserStatus;
import static java.util.stream.Collectors.toList;
import javax.enterprise.event.Event;


/**
 *
 * @author Florent
 */
@Stateless
public class GroupService {
    
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserDAO userDAO;
    @Inject ManageIndicesService indexService;
    @Inject FileIOService fileIOService;
    @Inject EnvironmentUtils environmentUtils;
    
    @Inject @GroupCreated
    private Event<Group> groupCreatedEvent;
    
    @Inject @GroupDeleted
    private Event<Group> groupDeletedEvent;
    
    public List<Group> initUserGroups( User user){
        return groupDAO.findAll().stream()
                    .map(g -> markAsSelected(g,user))
                    .map(g -> markAsPrimary(g,user))
                    .collect(Collectors.toList());
    }
 
    public List<Group> findByTenant(Tenant tenant){
        return groupDAO.findByTenant(tenant);
    }
    
    public Optional<Group> findByUuid(String uuid){
        return groupDAO.findById(uuid);
    }
    
    private Group setTenant(Group group){
        environmentUtils.getUserTenant()
                .ifPresent(t -> group.setTenant(t));
        return group;
    }
   
   
    private boolean isPrimary(Group group,User user){
        return userGroupDAO.findByUserAndGroup(user, group)
                    .map(UserGroup::getGroupType)
                    .filter(gt -> gt.equals(GroupType.PRIMARY))
                    .isPresent();
    }
        
    private Group markAsSelected(Group group,User user){
        if(belongTo(user, group)){
            group.setEdited(true);
        }
        return group;
    }
         
    private Group markAsPrimary(Group group,User user){
        if(isPrimary(group, user)){
            group.setPrimary(true);
        }
        return group;
    }

    private boolean belongTo(User user,Group group){
        return !userGroupDAO.findByUserAndGroup(user, group)
                .isEmpty();
    } 
   
    public Optional<Group> create(Group group) throws GroupNameExistException{
        Group grp = setTenant(group);
        var upperCaseName = StringUtil.upperCaseWithoutAccent(grp.getName());
        if(!isGroupWithNameExistInTenant(grp.getTenant(),upperCaseName)){
            Optional<Group> oGroupCreated = groupDAO.makePersistent((Group)StringUtil.nameToUpperCase(grp));
            oGroupCreated.ifPresent(groupCreatedEvent::fire);
            return oGroupCreated;
        }
        throw new GroupNameExistException("Ce nom de group existe déjà");
   }
    
    public Optional<Group> update(Group group) throws GroupNameExistException {
        var upperCaseName = StringUtil.upperCaseWithoutAccent(group.getName());
        Optional<Group> oGroupByName = groupDAO.findByTenantAndName(group.getTenant(), upperCaseName);
       
        if((oGroupByName.isPresent() && oGroupByName.filter(t1 -> t1.equals(group)).isEmpty()) ){
            throw new GroupNameExistException("Ce nom de group existe déjà");
        }
        return groupDAO.makePersistent((Group)StringUtil.nameToUpperCase(group));
    }
 
    private boolean isGroupWithNameExistInTenant(Tenant tenant,String name){
        Optional<Group> oTenant = groupDAO.findByTenantAndName(tenant, name);
        return oTenant.isPresent();
    }
   
    
    public void delete(Group group){
        disableOrphanUsers(group);
        deleteUsersGroups(group);
        deleteGroup(group);
        
        groupDeletedEvent.fire(group);
    }
    
    private void disableOrphanUsers(Group group){
        findUsersInGroup(group).stream().filter(this::isOnlyInCurrentGroup)
                .map(this::disable).forEach(userDAO::makePersistent);
    }
    private void deleteUsersGroups(Group group){
        userGroupDAO.findByGroup(group)
                .stream().forEach(userGroupDAO::makeTransient);
    }
    
    private void deleteGroup(Group group){
        Optional.ofNullable(group).ifPresent(groupDAO::makeTransient);
    }
    
    private List<User> findUsersInGroup(Group group){
        return userGroupDAO.findByGroup(group)
                .stream().map(UserGroup::getUser)
                .collect(toList());
    }
    
    private boolean isOnlyInCurrentGroup(User user){
        return  userGroupDAO.countAssociations(user) == 1;
    }
    
    private User disable(User user){
        user.setStatus(UserStatus.DISABLED);
        return user;
    }
  
}
