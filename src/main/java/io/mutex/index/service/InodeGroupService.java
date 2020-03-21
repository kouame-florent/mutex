/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.entity.Inode;
import io.mutex.index.entity.InodeGroup;
import io.mutex.user.entity.Group;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 *
 * @author florent
 */
public interface InodeGroupService {
    Optional<InodeGroup> create(@NotNull Inode inode,@NotNull Group group);
    Optional<InodeGroup> getByInodeAndGroup(@NotNull Inode inode,@NotNull Group group);
    List<InodeGroup> getAll();
    void delete(@NotNull Inode inode,@NotNull Group group);
}
