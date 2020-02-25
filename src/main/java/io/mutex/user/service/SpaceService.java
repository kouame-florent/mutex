/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.user.exception.AdminUserExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.TenantNameExistException;
import io.mutex.user.valueobject.TenantStatus;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author root
 */
public interface SpaceService {

    Space changeStatus(@NotNull Space tenant, @NotNull TenantStatus status);
    Optional<Space> create(@NotNull Space tenant) throws TenantNameExistException;
    void delete(@NotNull Space tenant);
    List<Space> findAllTenants();
    Optional<Space> findByName(@NotBlank String name);
    Optional<Space> findByUuid(@NotBlank String uuid);
    void unlinkAdminAndChangeStatus(@NotNull Space tenant);
    Optional<Space> update(@NotNull Space tenant) throws TenantNameExistException;
    void updateTenantAdmin(@NotNull Space tenant, @NotNull Admin adminUser) throws AdminUserExistException, NotMatchingPasswordAndConfirmation;
    
}
