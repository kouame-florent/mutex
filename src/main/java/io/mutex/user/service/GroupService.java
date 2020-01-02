/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;


import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
import io.mutex.index.service.IndexService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.NameUtils;
import io.mutex.user.entity.Tenant;
import io.mutex.user.exception.GroupNameExistException;
import io.mutex.user.repository.UserDAO;
import io.mutex.user.valueobject.UserStatus;
import static java.util.stream.Collectors.toList;


/**
 *
 * @author Florent
 */
@Stateless
public class GroupService {
    
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserDAO userDAO;
    @Inject IndexService indexService;
    @Inject FileIOService fileIOService;
    @Inject EnvironmentUtils environmentUtils;
    
    public List<Group> initUserGroups( User user){
        return groupDAO.findAll().stream()
                    .map(g -> setToBeEdited(g,user))
                    .map(g -> setPrimary(g,user))
                    .collect(Collectors.toList());
    }
    
//    public Optional<Group> createGroup(Group group){
//        
//    }
    
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
    
    private Group setPrimary(Group group,User user){
        if(isPrimary(group, user)){
            group.setPrimary(true);
        }
        return group;
    }
   
    private boolean isPrimary(Group group,User user){
        return userGroupDAO.findByUserAndGroup(user, group)
                    .map(UserGroup::getGroupType)
                    .filter(gt -> gt.equals(GroupType.PRIMARY))
                    .isPresent();
    }
    
    
    private Group setToBeEdited(Group group,User user){
        if(belongTo(user, group)){
            group.setEdited(true);
        }
        return group;
    }
    

    private boolean belongTo(User user,Group group){
        return !userGroupDAO.findByUserAndGroup(user, group)
                .isEmpty();
    } 
   
    public Optional<Group> createGroup(Group group) throws GroupNameExistException{
        Group grp = setTenant(group);
        var upperCaseName = NameUtils.upperCaseWithoutAccent(grp.getName());
        if(!isGroupWithNameExistInTenant(grp.getTenant(),upperCaseName)){
            return groupDAO.makePersistent((Group)NameUtils.nameToUpperCase(grp));
        }
        throw new GroupNameExistException("Ce nom de group existe déjà");
    
//        return setTenant(group).flatMap(groupDAO::makePersistent);
//        Optional<Group> grp = groupDAO.makePersistent(group);
//        grp.ifPresent(g -> indexService.createMetadataIndex(g));
//        grp.ifPresent(g -> indexService.createVirtualPageIndex(g));
//        grp.ifPresent(g -> indexService.createTermCompletionIndex(g));
//        grp.ifPresent(g -> indexService.createPhraseCompletionIndex(g));
//        grp.ifPresent(g -> indexService.tryCreateUtilIndex());
//        grp.ifPresent(g -> fileIOService.createGroupStoreDir(g));
//        indexService.tryCreateUtilIndex();
//        return grp ;    
    }
    
    public Optional<Group> updateGroup(Group group) throws GroupNameExistException {
        var upperCaseName = NameUtils.upperCaseWithoutAccent(group.getName());
        Optional<Group> oGroupByName = groupDAO.findByTenantAndName(group.getTenant(), upperCaseName);
       
        if((oGroupByName.isPresent() && oGroupByName.filter(t1 -> t1.equals(group)).isEmpty()) ){
            throw new GroupNameExistException("Ce nom de group existe déjà");
        }
        return groupDAO.makePersistent((Group)NameUtils.nameToUpperCase(group));
    }
 
    private boolean isGroupWithNameExistInTenant(Tenant tenant,String name){
        Optional<Group> oTenant = groupDAO.findByTenantAndName(tenant, name);
        return oTenant.isPresent();
    }
   
    
    public void delete(Group group){
        disableUsers(group);
        deleteUsersGroups(group);
        deleteGroup(group);
    }
    
    private void disableUsers(Group group){
        findUsersInGroup(group).stream().filter(this::isOnlyInCurrentGroup)
                .map(this::disable).forEach(userDAO::makePersistent);
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
    
    private void deleteUsersGroups(Group group){
        userGroupDAO.findByGroup(group)
                .stream().forEach(userGroupDAO::makeTransient);
    }
    
     private void deleteGroup(Group group){
        Optional.ofNullable(group).ifPresent(groupDAO::makeTransient);
    }
    
//    public void delete(Group group){
//        userGroupDAO.findByGroup(group).forEach(userGroupDAO::makeTransient);
//        groupDAO.makeTransient(group);
//    }
    
}
