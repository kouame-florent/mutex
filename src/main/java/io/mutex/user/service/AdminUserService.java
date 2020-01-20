/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.user.entity.UserRole;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.valueobject.UserStatus;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author root
 */
public interface AdminUserService {

    Optional<AdminUser> changeAdminUserStatus(AdminUser adminUser, UserStatus status);
    Optional<AdminUser> createAdminUser(AdminUser adminUser) throws AdminUserExistException, NotMatchingPasswordAndConfirmation;
    Optional<UserRole> createAdminUserRole(AdminUser adminUser);
    void delete(AdminUser adminUser);
    List<AdminUser> findAllAdminUsers();
    Optional<AdminUser> findByLogin(String login);
    Optional<AdminUser> findByTenant(Tenant tenant);
    Optional<AdminUser> findByUuid(String uuid);
    List<AdminUser> findNotAssignedToTenant();
    Optional<AdminUser> linkAdminUser(AdminUser adminUser, Tenant tenant);
    Optional<AdminUser> unlinkAdminUser(AdminUser adminUser);
    Optional<AdminUser> updateAdminUser(AdminUser adminUser) throws AdminLoginExistException, NotMatchingPasswordAndConfirmation;
    
}
