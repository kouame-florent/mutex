/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import io.mutex.shared.repository.GenericDAOImpl;
import javax.ejb.Stateless;
import io.mutex.user.entity.RootUser;
import io.mutex.shared.repository.GenericDAOImpl;

/**
 *
 * @author Florent
 */
@Stateless
public class RootUserDAOImpl extends GenericDAOImpl<RootUser, String> 
        implements RootUserDAO{
    
    public RootUserDAOImpl() {
        super(RootUser.class);
    }
    
}
