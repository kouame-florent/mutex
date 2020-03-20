/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.repository.InodeGroupDAO;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author florent
 */
@Stateless
public class InodeGroupServiceImpl implements InodeGroupService{
    
    @Inject InodeGroupDAO inodeGroupDAO;

    @Override
    public Optional<UserGroup> create(User user, Group group) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<UserGroup> getWithUserAndGroup(User user, Group group) {
        return inodeGroupDAO.
    }

    @Override
    public List<UserGroup> getAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
