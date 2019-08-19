/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;



import quantum.mutex.domain.entity.Role;
import quantum.mutex.domain.entity.RoleName;
import quantum.mutex.util.functional.Result;

/**
 *
 * @author Florent
 */
public interface RoleDAO extends GenericDAO<Role, String>{
    
    Result<Role> findByName(RoleName name);
}
