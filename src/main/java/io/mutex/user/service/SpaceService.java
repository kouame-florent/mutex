/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Space;
import io.mutex.user.exception.SpaceNameExistException;
import io.mutex.user.valueobject.SpaceStatus;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author root
 */
public interface SpaceService {

    Space changeStatus(@NotNull Space space, @NotNull SpaceStatus status);
    Optional<Space> create(@NotNull Space space) throws SpaceNameExistException;
    void delete(@NotNull Space space);
    List<Space> findAllSpaces();
    Optional<Space> findByName(@NotBlank String name);
    Optional<Space> findByUuid(@NotBlank String uuid);
//    void unlinkAdminAndChangeStatus(@NotNull Space space);
    Optional<Space> update(@NotNull Space space) throws SpaceNameExistException;
//    void updateSpaceAdmin(@NotNull Space space, @NotNull Admin admin) throws AdminExistException, NotMatchingPasswordAndConfirmation;
    
}
