/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.repository;

import java.util.List;
import java.util.Optional;
import mutex.shared.repository.GenericDAO;
import io.mutex.domain.AdminUser;
import io.mutex.domain.Tenant;



/**
 *
 * @author Florent
 */
public interface AdminUserDAO extends GenericDAO<AdminUser, String>{
    Optional<AdminUser> findByLogin(String login);
    List<AdminUser> findByTenant(Tenant tenant);
    List<AdminUser> findNotAssignedToTenant();
}
