/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import javax.ejb.Stateless;
import quantum.mutex.domain.UserGroup;

/**
 *
 * @author Florent
 */
@Stateless
public class UserGroupDAOImpl extends GenericDAOImpl<UserGroup, UserGroup.Id> 
        implements UserGroupDAO{
    
    public UserGroupDAOImpl() {
        super(UserGroup.class);
    }
    
}
