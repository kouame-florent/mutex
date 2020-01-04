/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.entity.Tenant;
import io.mutex.shared.repository.GenericDAO;


/**
 *
 * @author Florent
 */
public interface StandardUserDAO extends GenericDAO<StandardUser, String>{
    Optional<StandardUser> findByLogin(String login);
    List<StandardUser> findByTenant(Tenant tenant);
}
