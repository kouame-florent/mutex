/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.user.entity.Searcher;
import io.mutex.user.entity.Space;
import io.mutex.shared.repository.GenericDAO;


/**
 *
 * @author Florent
 */
public interface SearcherDAO extends GenericDAO<Searcher, String>{
    Optional<Searcher> findByLogin(String login);
//    List<Searcher> findBySpace(Space space);
//    List<Searcher> findAllOrderBySpace();
    List<Searcher> findAllOrderByName();
}
