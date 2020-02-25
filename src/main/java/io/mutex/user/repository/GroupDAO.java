/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Space;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.User;


/**
 *
 * @author Florent
 */
public interface GroupDAO extends GenericDAO<Group, String>{
    
    Optional<Group> findBySpaceAndName(Space space,String name);
    List<Group> findBySpace(Space space);
    List<Group> findByUser(User user);
  
}
