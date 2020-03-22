/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Role;
import io.mutex.user.repository.RoleDAO;
import io.mutex.user.valueobject.RoleName;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author florent
 */
@Stateless
public class RoleServiceImpl implements RoleService{
    
    @Inject RoleDAO roleDAO;

    @Override
    public Optional<Role> getByName(RoleName roleName) {
        return roleDAO.findByName(roleName);
    }
    
}
