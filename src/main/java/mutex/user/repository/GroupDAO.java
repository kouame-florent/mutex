/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.repository;

import java.util.List;
import java.util.Optional;
import mutex.shared.repository.GenericDAO;
import mutex.user.domain.entity.Group;
import mutex.user.domain.entity.Tenant;
import mutex.user.domain.entity.User;


/**
 *
 * @author Florent
 */
public interface GroupDAO extends GenericDAO<Group, String>{
    
    Optional<Group> findByTenantAndName(Tenant tenant,String name);
    List<Group> findByTenant(Tenant tenant);
    List<Group> findByUser(User user);
  
    
}
