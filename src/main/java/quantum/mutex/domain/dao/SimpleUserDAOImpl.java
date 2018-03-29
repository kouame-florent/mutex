/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import javax.ejb.Stateless;
import quantum.mutex.domain.StandardUser;

/**
 *
 * @author Florent
 */
@Stateless
public class SimpleUserDAOImpl extends GenericDAOImpl<StandardUser, UUID> implements SimpleUserDAO{
    
    public SimpleUserDAOImpl() {
        super(StandardUser.class);
    }
    
}
