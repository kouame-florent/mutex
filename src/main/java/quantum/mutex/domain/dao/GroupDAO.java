/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.entity.User;
import quantum.mutex.util.functional.Result;

/**
 *
 * @author Florent
 */
public interface GroupDAO extends GenericDAO<Group, String>{
    
    Result<Group> findByTenantAndName(Tenant tenant,String name);
    List<Group> findByTenant(Tenant tenant);
    List<Group> findByUser(User user);
  
    
}
