/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import quantum.mutex.common.Result;
import quantum.mutex.domain.Role;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserRole;

/**
 *
 * @author Florent
 */
public interface UserRoleDAO extends GenericDAO<UserRole, UserRole.Id>{
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    Result<UserRole> findByUserAndRole(User user,Role role);
}
