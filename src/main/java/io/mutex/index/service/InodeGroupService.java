/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.user.entity.Group;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 *
 * @author florent
 */
public interface InodeGroupService {
    Optional<UserGroup> create(@NotNull User user,@NotNull Group group);
    Optional<UserGroup> getWithUserAndGroup(@NotNull User user,@NotNull Group group);
    List<UserGroup> getAll();
}
