/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import quantum.mutex.common.Result;
import quantum.mutex.domain.StandardUser;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.User;

/**
 *
 * @author Florent
 */
public interface StandardUserDAO extends GenericDAO<StandardUser, UUID>{
    Result<User> findByLogin(String login);
    List<User> findByTenant(Tenant tenant);
}
