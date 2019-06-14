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
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.entity.User;

/**
 *
 * @author Florent
 */
public interface UserDAO extends GenericDAO<User, String>{
    
    Result<User> findByLogin(String login);
    List<User> findByTenant(Tenant tenant);
    List<User> findAllUser(Group group);
}
