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
import io.mutex.index.service.IndicesService;
import io.mutex.shared.event.GroupCreated;
import io.mutex.shared.event.GroupDeleted;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
import io.mutex.user.entity.Space;
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
public class GroupServiceImpl implements GroupService {
    
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserDAO userDAO;
    @Inject IndicesService indexService;
    @Inject FileIOService fileIOService;
    @Inject EnvironmentUtils environmentUtils;
    
    @Inject @GroupCreated
    private Event<Group> groupCreatedEvent;
    
    @Inject @GroupDeleted
    private Event<Group> groupDeletedEvent;
    
    @Override
    public List<Group> initUserGroups( User user){
        return groupDAO.findAll().stream()
                    .map(g -> markAsSelected(g,user))
                    .map(g -> markAsPrimary(g,user))
                    .collect(Collectors.toList());
    }
 
    @Override
    public List<Group> findBySpace(Space space){
        return groupDAO.findBySpace(space);
    }
    
    
    @Override
    public Optional<Group> findBySpaceAndName(Space space, String name) {
       return groupDAO.findBySpaceAndName(space, name);
    }
        
    @Override
    public Optional<Group> findByUuid(String uuid){
        return groupDAO.findById(uuid);
    }
    
    @Override
    public List<Group> findAll() {
        return groupDAO.findAll();
    }
  
    
//    private Group setSpace(Group group){
//        environmentUtils.getUserSpace()
//                .ifPresent(t -> group.setSpace(t));
//        return group;
//    }
   
   
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
   
    @Override
    public Optional<Group> create(Group group) throws GroupNameExistException{
//        Group grp = setSpace(group);
        var upperCaseName = StringUtil.upperCaseWithoutAccent(group.getName());
        if(!isGroupWithNameExistInSpace(group.getSpace(),upperCaseName)){
            Optional<Group> oGroupCreated = groupDAO.makePersistent((Group)StringUtil.nameToUpperCase(group));
            oGroupCreated.ifPresent(groupCreatedEvent::fire);
            return oGroupCreated;
        }
        throw new GroupNameExistException("Ce nom de group existe déjà");
   }
    
    @Override
    public Optional<Group> update(Group group) throws GroupNameExistException {
        var upperCaseName = StringUtil.upperCaseWithoutAccent(group.getName());
        Optional<Group> oGroupByName = groupDAO.findBySpaceAndName(group.getSpace(), upperCaseName);
       
        if((oGroupByName.isPresent() && oGroupByName.filter(t1 -> t1.equals(group)).isEmpty()) ){
            throw new GroupNameExistException("Ce nom de group existe déjà");
        }
        return groupDAO.makePersistent((Group)StringUtil.nameToUpperCase(group));
    }
 
    private boolean isGroupWithNameExistInSpace(Space space,String name){
        Optional<Group> oSpace = groupDAO.findBySpaceAndName(space, name);
        return oSpace.isPresent();
    }
   
    
    @Override
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
