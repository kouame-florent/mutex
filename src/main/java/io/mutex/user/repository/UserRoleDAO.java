/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.Role;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;


/**
 *
 * @author Florent
 */
public interface UserRoleDAO extends GenericDAO<UserRole, String>{
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    Optional<UserRole> findByUserAndRole(String userLogin,String roleName);
}
