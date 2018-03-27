/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import javax.ejb.Stateless;
import quantum.mutex.domain.SuperUser;

/**
 *
 * @author Florent
 */
@Stateless
public class SuperUserDAOImpl extends GenericDAOImpl<SuperUser, UUID> implements SuperUserDAO{
     
    public SuperUserDAOImpl() {
        super(SuperUser.class);
    }
     
}
