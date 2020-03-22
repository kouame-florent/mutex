/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Role;
import io.mutex.user.valueobject.RoleName;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 *
 * @author florent
 */
public interface RoleService {
    Optional<Role> getByName(@NotNull RoleName roleName);
}
