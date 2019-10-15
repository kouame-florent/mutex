/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.service;


import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.valueobject.GroupType;
import quantum.mutex.user.domain.entity.User;
import quantum.mutex.user.domain.entity.UserGroup;
import quantum.mutex.user.repository.GroupDAO;
import quantum.mutex.user.repository.RoleDAO;
import quantum.mutex.user.repository.UserDAO;
import quantum.mutex.user.repository.UserGroupDAO;
import quantum.mutex.user.repository.UserRoleDAO;



/**
 *
 * @author Florent
 */
@Stateless
public class UserService {

    private static final Logger LOG = Logger.getLogger(UserService.class.getName());
    
    @Inject UserDAO userDAO;
    @Inject GroupDAO groupDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserRoleDAO userRoleDAO;
    @Inject UserGroupDAO userGroupDAO;
    
    public void save( User user, List<Group> selectedGroups){
        Optional<User> optMngUser = userDAO.makePersistent(user);
        optMngUser.map(u -> createUserGroups(u, selectedGroups));
    }
    
    private List<UserGroup> createUserGroups(User user, List<Group> groups){
        return groups.stream().map(g -> newUserGroup(user, g))
                    .map(rug -> rug.orElseGet(() -> new UserGroup()))
                    .collect(Collectors.toList());
    }
     
    private Optional<UserGroup> newUserGroup(User user, Group group){
      if(!hasPrimaryGroup(user)){
          return userGroupDAO.makePersistent(new UserGroup(user, group, GroupType.PRIMARY));
      }
      return userGroupDAO.makePersistent(new UserGroup(user, group,GroupType.SECONDARY));
    }
    
    private boolean hasPrimaryGroup(User user){
        return !userGroupDAO
                .findUserPrimaryGroup(user).isEmpty();
    }
    
    

    
//    public User update(User user,List<Group> selectedGroups){
////        User managedUser = userDAO.makePersistent(user);
////        List<Group> userGroups = userGroupDAO.findByUser(managedUser)
////                    .stream().map(ug -> ug.getGroup()).collect(Collectors.toList());
////        appendGroup(managedUser, selectedGroups, userGroups);
////        removeGroup(managedUser, selectedGroups, userGroups);
//        
//        return user;
//    }
//    
//    private void appendGroup(User managedUser,List<Group> selectedGroups,List<Group> userGroups){
//        
////        selectedGroups.stream().filter(g -> !userGroups.contains(g))
////                .forEach(g -> {  
////                    Group managedGroup = groupDAO.makePersistent(g);
////                    if(userGroupDAO.findByUserAndGroupType(managedUser, 
////                            GroupType.PRIMARY).isEmpty()){
////                        userGroupDAO.makePersistent(new UserGroup(managedUser, managedGroup, GroupType.PRIMARY));
////                    }else{
////                        userGroupDAO.makePersistent(new UserGroup(managedUser, managedGroup, GroupType.SECONDARY));
////                    }
////                });
//      
//    }
//    
//    private void removeGroup(User managedUser,List<Group> selectedGroups,List<Group> userGroups){
//        
////        userGroups.stream().filter(g -> !selectedGroups.contains(g))
////                .forEach(g -> { 
////                    Group managedGroup = groupDAO.makePersistent(g);
////                    UserGroup managedUserGroup = userGroupDAO.findByUserAndGroup(managedUser, managedGroup).get();
////                    userGroupDAO.makeTransient(managedUserGroup);
////                });
////        checkAndSetGroupType(managedUser, GroupType.PRIMARY);
//     }
//    
//    private void checkAndSetGroupType(User user){
////        if(userGroupDAO.findUserPrimaryGroup(user).isEmpty()){
////            userGroupDAO.findByUser(user).stream()
////                    .findFirst().ifPresent(ugr -> ugr.setGroupType(groupType));
////        }
//    }
    
}
