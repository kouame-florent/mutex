/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.Optional;
import java.util.UUID;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Role;
import quantum.mutex.domain.RoleName;

/**
 *
 * @author Florent
 */
public interface RoleDAO extends GenericDAO<Role, UUID>{
    
    Result<Role> findByName(RoleName name);
}
