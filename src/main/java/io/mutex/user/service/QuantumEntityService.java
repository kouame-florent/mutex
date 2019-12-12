/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.shared.entity.BaseEntity;
import java.util.Optional;

/**
 *
 * @author root
 * @param <T>
 */
public interface QuantumEntityService<T extends BaseEntity> {
    
    public Optional<T> create(T entity,Object... options);
    public Optional<T> update(T entity,Object... options);
    public void delete(T entity,Object... options);
}
