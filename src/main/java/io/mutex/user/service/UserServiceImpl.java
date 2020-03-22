/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.User;
import io.mutex.user.repository.UserDAO;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author florent
 */
@Stateless
public class UserServiceImpl implements UserService{
    
    @Inject UserDAO userDAO;

    @Override
    public Optional<User> getByUUID(@NotBlank String userUUID) {
       return userDAO.findById(userUUID);
    }

    @Override
    public void delete(@NotNull User user) {
       userDAO.makeTransient(user);
    }

    @Override
    public Optional<User> getByLogin(@NotBlank String login) {
        return userDAO.findByLogin(login);
    }
    
}
