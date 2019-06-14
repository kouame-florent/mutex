/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Role;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserRole;

/**
 *
 * @author Florent
 */
public interface UserRoleDAO extends GenericDAO<UserRole, String>{
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    Result<UserRole> findByUserAndRole(String userLogin,String roleName);
}
