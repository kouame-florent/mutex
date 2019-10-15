/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.repository;



import quantum.mutex.shared.repository.GenericDAO;
import java.util.Optional;
import quantum.mutex.user.domain.entity.Role;
import quantum.mutex.user.domain.valueobject.RoleName;


/**
 *
 * @author Florent
 */
public interface RoleDAO extends GenericDAO<Role, String>{
    
    Optional<Role> findByName(RoleName name);
}
