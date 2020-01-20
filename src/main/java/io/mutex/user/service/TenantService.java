/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
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
public interface TenantService {

    Tenant changeStatus(@NotNull Tenant tenant, @NotNull TenantStatus status);
    Optional<Tenant> create(@NotNull Tenant tenant) throws TenantNameExistException;
    void delete(@NotNull Tenant tenant);
    List<Tenant> findAllTenants();
    Optional<Tenant> findByName(@NotBlank String name);
    Optional<Tenant> findByUuid(@NotBlank String uuid);
    void unlinkAdminAndChangeStatus(@NotNull Tenant tenant);
    Optional<Tenant> update(@NotNull Tenant tenant) throws TenantNameExistException;
    void updateTenantAdmin(@NotNull Tenant tenant, @NotNull AdminUser adminUser) throws AdminUserExistException, NotMatchingPasswordAndConfirmation;
    
}
