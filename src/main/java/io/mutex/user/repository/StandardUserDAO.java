/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import io.mutex.shared.repository.GenericDAO;
import java.util.List;
import java.util.Optional;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.entity.Tenant;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.User;


/**
 *
 * @author Florent
 */
public interface StandardUserDAO extends GenericDAO<StandardUser, String>{
    Optional<User> findByLogin(String login);
    List<User> findByTenant(Tenant tenant);
}
