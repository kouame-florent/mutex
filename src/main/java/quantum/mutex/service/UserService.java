/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
    
}
