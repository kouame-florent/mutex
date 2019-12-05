/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.repository;

import io.mutex.repository.GenericDAOImpl;
import javax.ejb.Stateless;
import io.mutex.domain.entity.RootUser;

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
