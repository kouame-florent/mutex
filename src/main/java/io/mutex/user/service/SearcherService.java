/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Searcher;
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
public interface SearcherService {

    Optional<Searcher> create(@NotNull Searcher user) throws NotMatchingPasswordAndConfirmation, UserLoginExistException;
    void delete(@NotNull Searcher user);
    void disable(@NotNull Searcher user);
    void enable(@NotNull Searcher user);
//    List<Searcher> findBySpace(Space space);
//    List<Searcher> findAllOrderBySpace();
    List<Searcher> findAllOrderByName();
    Optional<Searcher> findByUuid(@NotBlank String uuid);
    Optional<Searcher> update(@NotNull Searcher user) throws NotMatchingPasswordAndConfirmation;
    
}
