/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.repository;

import mutex.shared.repository.GenericDAO;
import java.util.List;
import java.util.Optional;
import mutex.user.domain.entity.StandardUser;
import mutex.user.domain.entity.Tenant;
import mutex.user.domain.entity.User;


/**
 *
 * @author Florent
 */
public interface StandardUserDAO extends GenericDAO<StandardUser, String>{
    Optional<User> findByLogin(String login);
    List<User> findByTenant(Tenant tenant);
}
