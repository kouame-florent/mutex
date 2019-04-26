/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.domain;


import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.GroupType;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserGroup;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.service.FileIOService;
import quantum.mutex.service.search.IndexService;

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
    
    public List<Group> initUserGroups(@NotNull User user){
        return groupDAO.findAll().stream()
                    .map(g -> setToBeEdited.apply(g).apply(user))
                    .map(g -> setPrimary.apply(g).apply(user))
                    .collect(Collectors.toList());
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
   
    public Result<Group> initGroup(Group group){
        Result<Group> grp = groupDAO.makePersistent(group);
        grp.forEach(g -> indexService.createMetadataIndex(g));
        grp.forEach(g -> indexService.createVirtualPageIndex(g));
        grp.forEach(g -> indexService.createTermCompletionIndex(g));
        grp.forEach(g -> indexService.createPhraseCompletionIndex(g));
        grp.forEach(g -> indexService.tryCreateUtilIndex());
        grp.forEach(g -> fileIOService.createGroupStoreDir(g));
        indexService.tryCreateUtilIndex();
        return grp ;    
    }
 
    
    public void delete(Group group){
        userGroupDAO.findByGroup(group).forEach(userGroupDAO::makeTransient);
        groupDAO.makeTransient(group);
    }
    
}
