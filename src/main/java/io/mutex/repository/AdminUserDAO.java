/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.repository.GenericDAO;
import io.mutex.domain.entity.AdminUser;
import io.mutex.domain.entity.Tenant;



/**
 *
 * @author Florent
 */
public interface AdminUserDAO extends GenericDAO<AdminUser, String>{
    Optional<AdminUser> findByLogin(String login);
    List<AdminUser> findByTenant(Tenant tenant);
    List<AdminUser> findNotAssignedToTenant();
}
