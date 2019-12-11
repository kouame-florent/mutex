/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Tenant;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.User;


/**
 *
 * @author Florent
 */
public interface GroupDAO extends GenericDAO<Group, String>{
    
    Optional<Group> findByTenantAndName(Tenant tenant,String name);
    List<Group> findByTenant(Tenant tenant);
    List<Group> findByUser(User user);
  
    
}
