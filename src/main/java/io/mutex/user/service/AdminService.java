/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
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
public interface AdminService {

    Optional<Admin> changeAdminUserStatus(Admin adminUser, UserStatus status);
    Optional<Admin> createAdminUser(Admin adminUser) throws AdminUserExistException, NotMatchingPasswordAndConfirmation;
    Optional<UserRole> createAdminUserRole(Admin adminUser);
    void delete(Admin adminUser);
    List<Admin> findAllAdminUsers();
    Optional<Admin> findByLogin(String login);
    Optional<Admin> findBySpace(Space space);
    Optional<Admin> findByUuid(String uuid);
//    List<Admin> findNotAssignedToSpace();
//    Optional<Admin> linkAdminUser(Admin adminUser, Space space);
//    Optional<Admin> unlinkAdminUser(Admin adminUser);
    Optional<Admin> updateAdminUser(Admin adminUser) throws AdminLoginExistException, NotMatchingPasswordAndConfirmation;
    
}
