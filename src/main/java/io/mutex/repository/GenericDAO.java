/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.repository;

import io.mutex.domain.entity.BaseEntity;
import java.util.List;
import java.util.Optional;




/**
 *
 * @author florent
 * @param <E>
 * @param <ID>
 * 
 */
public interface GenericDAO<E,ID>  {
    
    Optional<E> makePersistent(E entity); 
    Optional<E> makeTransient(E entity);
    Optional<E> findById(ID id);
    Optional<E> findReferenceById(ID id);
    List<E> findAll();
    Long getCount();
    
    <T extends BaseEntity> Optional<E> findBy(T param);
}
