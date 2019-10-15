/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.domain.entity.User;
import quantum.mutex.user.domain.valueobject.UserStatus;


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
