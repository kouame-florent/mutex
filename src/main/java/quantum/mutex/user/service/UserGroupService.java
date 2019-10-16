/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.user.repository.GroupDAO;
import quantum.mutex.user.repository.UserGroupDAO;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.entity.User;
import quantum.mutex.user.domain.entity.UserGroup;

/**
 *
 * @author Florent
 */
@Stateless
public class UserGroupService {
    
    @Inject UserGroupDAO userGroupDAO;
    @Inject GroupDAO groupDAO;
    
    public List<Group> getAllGroups(User user){
        return userGroupDAO.findByUser(user).stream()
                    .map(ug -> groupDAO.findById(ug.getGroup().getUuid()))
                    .flatMap(Optional::stream)
                    .collect(toList());
   }
    
    public List<Group> getGroups( User user){
        return userGroupDAO.findByUser(user)
                .stream().map(UserGroup::getGroup)
                .collect(Collectors.toList());
    }
}
