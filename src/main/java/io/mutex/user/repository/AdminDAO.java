/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.shared.repository.GenericDAO;



/**
 *
 * @author Florent
 */
public interface AdminDAO extends GenericDAO<Admin, String>{
    Optional<Admin> findByLogin(String login);
    Optional<Admin> findBySpace(Space space);
//    List<Admin> findNotAssignedToSpace();
}
