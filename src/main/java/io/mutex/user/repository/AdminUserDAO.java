/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.shared.repository.GenericDAO;



/**
 *
 * @author Florent
 */
public interface AdminUserDAO extends GenericDAO<AdminUser, String>{
    Optional<AdminUser> findByLogin(String login);
    List<AdminUser> findByTenant(Tenant tenant);
    List<AdminUser> findNotAssignedToTenant();
}
