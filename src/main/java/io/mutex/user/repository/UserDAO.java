/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Tenant;
import io.mutex.user.entity.User;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.shared.repository.GenericDAO;


/**
 *
 * @author Florent
 */
public interface UserDAO extends GenericDAO<User, String>{
    
    Optional<User> findByLogin(String login);
//    Optional<User> findByLoginAndPassword(String login,String password);
    Optional<User> findWithStatus(String login,String password,UserStatus status);
    List<User> findByTenant(Tenant tenant);
    List<User> findAllUser(Group group);
}
