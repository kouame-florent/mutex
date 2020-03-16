/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.shared.event.SpaceDeleted;
import io.mutex.user.entity.Space;
import javax.enterprise.event.Observes;
import javax.validation.constraints.NotNull;

/**
 *
 * @author florent
 */
public interface DeletionEventService {
    void handleSpaceDeletion(@Observes @SpaceDeleted @NotNull Space space);

}
