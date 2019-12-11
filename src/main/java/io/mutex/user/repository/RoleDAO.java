/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;



import io.mutex.shared.repository.GenericDAO;
import java.util.Optional;
import io.mutex.user.entity.Role;
import io.mutex.search.valueobject.RoleName;
import io.mutex.shared.repository.GenericDAO;


/**
 *
 * @author Florent
 */
public interface RoleDAO extends GenericDAO<Role, String>{
    
    Optional<Role> findByName(RoleName name);
}
