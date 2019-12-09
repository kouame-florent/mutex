/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service.user;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author root
 * @param <T>
 */
public interface MainService<T> {
    Optional<T> createEntity(T entity);
    Optional<T> updateEntity(T entity);
    void deleteEntity();
    Optional<T> findByUUID(String uuid);
    List<T> findAllEntities();
}
