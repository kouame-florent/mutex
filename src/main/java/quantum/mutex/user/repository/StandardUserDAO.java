/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.user.repository;

import quantum.mutex.shared.repository.GenericDAO;
import java.util.List;
import java.util.Optional;
import quantum.mutex.user.domain.entity.StandardUser;
import quantum.mutex.user.domain.entity.Tenant;
import quantum.mutex.user.domain.entity.User;


/**
 *
 * @author Florent
 */
public interface StandardUserDAO extends GenericDAO<StandardUser, String>{
    Optional<User> findByLogin(String login);
    List<User> findByTenant(Tenant tenant);
}
