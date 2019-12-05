/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.repository;

import mutex.shared.repository.GenericDAO;
import java.util.List;
import java.util.Optional;
import io.mutex.domain.StandardUser;
import io.mutex.domain.Tenant;
import io.mutex.domain.User;


/**
 *
 * @author Florent
 */
public interface StandardUserDAO extends GenericDAO<StandardUser, String>{
    Optional<User> findByLogin(String login);
    List<User> findByTenant(Tenant tenant);
}
