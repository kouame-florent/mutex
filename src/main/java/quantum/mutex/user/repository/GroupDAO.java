/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import quantum.mutex.shared.repository.GenericDAO;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.domain.entity.User;


/**
 *
 * @author Florent
 */
public interface GroupDAO extends GenericDAO<Group, String>{
    
    Optional<Group> findByTenantAndName(Tenant tenant,String name);
    List<Group> findByTenant(Tenant tenant);
    List<Group> findByUser(User user);
  
    
}
