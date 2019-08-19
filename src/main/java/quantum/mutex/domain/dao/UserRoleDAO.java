/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import quantum.mutex.domain.entity.Role;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserRole;
import quantum.mutex.util.functional.Result;

/**
 *
 * @author Florent
 */
public interface UserRoleDAO extends GenericDAO<UserRole, String>{
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    Result<UserRole> findByUserAndRole(String userLogin,String roleName);
}
