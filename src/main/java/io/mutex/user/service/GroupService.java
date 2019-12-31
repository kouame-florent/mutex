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
import io.mutex.user.entity.Tenant;


/**
 *
 * @author Florent
 */
@Stateless
public class GroupService {
    
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject IndexService indexService;
    @Inject FileIOService fileIOService;
    
    public List<Group> initUserGroups( User user){
        return groupDAO.findAll().stream()
                    .map(g -> setToBeEdited.apply(g).apply(user))
                    .map(g -> setPrimary.apply(g).apply(user))
                    .collect(Collectors.toList());
    }
    
    public List<Group> findByTenant(Tenant tenant){
        return groupDAO.findByTenant(tenant);
    }
    
    private final Function<Group,Function<User,Group>> setPrimary = group -> 
        user -> {
            if(this.isPrimary.apply(group).apply(user)){
                group.setPrimary(true);
            }
            return group;
    };
            
    
    private Function<Group, Function<User,Boolean>> isPrimary = group -> user ->
           !userGroupDAO.findByUserAndGroup(user, group).map(UserGroup::getGroupType)
                    .filter(gt -> gt.equals(GroupType.PRIMARY))
                    .isEmpty();
    
    private final Function<Group,Function<User,Group>> setToBeEdited = group -> 
        user -> {
            if(belongTo(user, group)){
                group.setEdited(true);
            }
             return group;
    };
     
    private boolean belongTo(User user,Group group){
        return !userGroupDAO.findByUserAndGroup(user, group)
                .isEmpty();
    } 
   
    public Optional<Group> initGroup(Group group){
        Optional<Group> grp = groupDAO.makePersistent(group);
        grp.ifPresent(g -> indexService.createMetadataIndex(g));
        grp.ifPresent(g -> indexService.createVirtualPageIndex(g));
        grp.ifPresent(g -> indexService.createTermCompletionIndex(g));
        grp.ifPresent(g -> indexService.createPhraseCompletionIndex(g));
        grp.ifPresent(g -> indexService.tryCreateUtilIndex());
        grp.ifPresent(g -> fileIOService.createGroupStoreDir(g));
        indexService.tryCreateUtilIndex();
        return grp ;    
    }
 
    
    public void delete(Group group){
        userGroupDAO.findByGroup(group).forEach(userGroupDAO::makeTransient);
        groupDAO.makeTransient(group);
    }
    
}
