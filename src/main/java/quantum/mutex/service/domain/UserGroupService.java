/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.domain;

import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.User;

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
                    .filter(r -> !r.isEmpty())
                    .map(g -> g.successValue())
                    .collect(toList());
    }
    
}
