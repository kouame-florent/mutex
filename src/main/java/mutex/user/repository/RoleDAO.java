/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.repository;



import mutex.shared.repository.GenericDAO;
import java.util.Optional;
import mutex.user.domain.entity.Role;
import mutex.user.domain.valueobject.RoleName;


/**
 *
 * @author Florent
 */
public interface RoleDAO extends GenericDAO<Role, String>{
    
    Optional<Role> findByName(RoleName name);
}
