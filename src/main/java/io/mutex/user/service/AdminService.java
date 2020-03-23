/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Admin;
import io.mutex.user.entity.UserRole;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.valueobject.UserStatus;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author root
 */
public interface AdminService {

    Optional<Admin> changeAdminStatus(Admin admin, UserStatus status);
    Optional<Admin> createAdmin(Admin admin) throws AdminExistException, NotMatchingPasswordAndConfirmation;
    Optional<UserRole> createAdminRole(Admin admin);
    void deleteAdmin(Admin admin);
    List<Admin> findAllAdmins();
    Optional<Admin> findByLogin(String login);
//    Optional<Admin> findBySpace(Space space);
    Optional<Admin> findByUuid(String uuid);
//    List<Admin> findNotAssignedToSpace();
//    Optional<Admin> linkAdmin(Admin admin, Space space);
//    Optional<Admin> unlinkAdmin(Admin admin);
    Optional<Admin> updateAdmin(Admin admin) throws AdminLoginExistException, NotMatchingPasswordAndConfirmation;
    
}
