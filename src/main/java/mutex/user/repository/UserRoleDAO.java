/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.repository;

import java.util.List;
import java.util.Optional;
import mutex.shared.repository.GenericDAO;
import io.mutex.domain.Role;
import io.mutex.domain.User;
import io.mutex.domain.UserRole;


/**
 *
 * @author Florent
 */
public interface UserRoleDAO extends GenericDAO<UserRole, String>{
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    Optional<UserRole> findByUserAndRole(String userLogin,String roleName);
}
