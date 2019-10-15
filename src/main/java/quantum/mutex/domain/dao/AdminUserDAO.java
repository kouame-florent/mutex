/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import quantum.mutex.user.domain.entity.AdminUser;
import quantum.mutex.user.domain.entity.Tenant;



/**
 *
 * @author Florent
 */
public interface AdminUserDAO extends GenericDAO<AdminUser, String>{
    Optional<AdminUser> findByLogin(String login);
    List<AdminUser> findByTenant(Tenant tenant);
    List<AdminUser> findNotAssignedToTenant();
}
