/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.Tenant;

/**
 *
 * @author Florent
 */
public interface GroupDAO extends GenericDAO<Group, UUID>{
    
    Optional<Group> findByTenantAndName(Tenant tenant,String name);
    List<Group> findByTenant(Tenant tenant);
    
}
