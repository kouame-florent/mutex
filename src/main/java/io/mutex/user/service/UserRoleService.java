/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.User;
import io.mutex.user.entity.UserRole;
import io.mutex.user.valueobject.RoleName;
import java.util.List;
import java.util.Optional;
import javax.ejb.Asynchronous;

/**
 *
 * @author root
 */
public interface UserRoleService {

    @Asynchronous
    void cleanOrphansUserRole();
    Optional<UserRole> create(User user, RoleName roleName);
    List<UserRole> getByUser(User user);
    void delete(UserRole ur);
    
}
