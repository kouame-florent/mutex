/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.User;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author florent
 */
public interface UserService {
    
    Optional<User> getByUUID(@NotBlank String userUUID);
    Optional<User> getByLogin(@NotBlank String login);
    void delete(@NotNull User user);
}
