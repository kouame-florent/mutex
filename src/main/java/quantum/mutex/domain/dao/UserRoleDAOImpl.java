/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import javax.ejb.Stateless;
import quantum.mutex.domain.UserRole;

/**
 *
 * @author Florent
 */
@Stateless
public class UserRoleDAOImpl extends GenericDAOImpl<UserRole, UserRole.Id> 
        implements UserRoleDAO{
    
    public UserRoleDAOImpl() {
        super(UserRole.class);
    }
    
}
