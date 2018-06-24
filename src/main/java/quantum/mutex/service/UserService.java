/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.GroupType;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;

/**
 *
 * @author Florent
 */
@Stateless
public class UserService {

    private static final Logger LOG = Logger.getLogger(UserService.class.getName());
    
    
    @Inject UserDAO userDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    
    public User save(User user,List<Group> selectedGroups){
        User managedUser = userDAO.makePersistent(user);
        selectedGroups.forEach(g -> { 
            Group managedGroup = groupDAO.makePersistent(g);
            if(userGroupDAO.findByUserAndGroupType(managedUser, 
                    GroupType.PRIMARY).isEmpty()){
                userGroupDAO.makePersistent(new UserGroup(managedUser, managedGroup, GroupType.PRIMARY));
            }else{
                userGroupDAO.makePersistent(new UserGroup(managedUser, managedGroup, GroupType.SECONDARY));
            }
            
        });
        
        return managedUser;
    }
    
//    public AdminUser saveAdmin(AdminUser adminUser){
//        
//    }
    
    public User update(User user,List<Group> selectedGroups){
        User managedUser = userDAO.makePersistent(user);
        List<Group> userGroups = userGroupDAO.findByUser(managedUser)
                    .stream().map(ug -> ug.getGroup()).collect(Collectors.toList());
        appendGroup(managedUser, selectedGroups, userGroups);
        removeGroup(managedUser, selectedGroups, userGroups);
        
        return managedUser;
    }
    
    private void appendGroup(User managedUser,List<Group> selectedGroups,List<Group> userGroups){
        
        selectedGroups.stream().filter(g -> !userGroups.contains(g))
                .forEach(g -> {  
                    Group managedGroup = groupDAO.makePersistent(g);
                    if(userGroupDAO.findByUserAndGroupType(managedUser, 
                            GroupType.PRIMARY).isEmpty()){
                        userGroupDAO.makePersistent(new UserGroup(managedUser, managedGroup, GroupType.PRIMARY));
                    }else{
                        userGroupDAO.makePersistent(new UserGroup(managedUser, managedGroup, GroupType.SECONDARY));
                    }
                });
      
    }
    
    private void removeGroup(User managedUser,List<Group> selectedGroups,List<Group> userGroups){
        
        userGroups.stream().filter(g -> !selectedGroups.contains(g))
                .forEach(g -> { 
                    Group managedGroup = groupDAO.makePersistent(g);
                    UserGroup managedUserGroup = userGroupDAO.findByUserAndGroup(managedUser, managedGroup).get();
                    userGroupDAO.makeTransient(managedUserGroup);
                });
        checkAndSetGroupType(managedUser, GroupType.PRIMARY);
     }
    
    private void checkAndSetGroupType(User user,GroupType groupType){
        if(userGroupDAO.findByUserAndGroupType(user, groupType).isEmpty()){
            userGroupDAO.findByUser(user).stream()
                    .findFirst().ifPresent(ugr -> ugr.setGroupType(groupType));
        }
    }
    
}
