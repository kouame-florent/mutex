/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Group;
import io.mutex.user.entity.Space;
import io.mutex.user.entity.User;
import io.mutex.user.exception.GroupNameExistException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author root
 */
public interface GroupService {

    Optional<Group> create(Group group) throws GroupNameExistException;
    void delete(Group group);
    List<Group> findByTenant(Space tenant);
    Optional<Group> findByUuid(String uuid);
    List<Group> initUserGroups(User user);
    Optional<Group> update(Group group) throws GroupNameExistException;
    
}
