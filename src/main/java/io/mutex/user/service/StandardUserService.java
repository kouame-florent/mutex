/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.StandardUser;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.UserLoginExistException;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author root
 */
public interface StandardUserService {

    Optional<StandardUser> create(@NotNull StandardUser user) throws NotMatchingPasswordAndConfirmation, UserLoginExistException;
    void delete(@NotNull StandardUser user);
    void disable(@NotNull StandardUser user);
    void enable(@NotNull StandardUser user);
    List<StandardUser> findByTenant();
    Optional<StandardUser> findByUuid(@NotBlank String uuid);
    Optional<StandardUser> update(@NotNull StandardUser user) throws NotMatchingPasswordAndConfirmation;
    
}
