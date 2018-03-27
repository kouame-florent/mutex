/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import javax.ejb.Stateless;
import quantum.mutex.domain.Group;

/**
 *
 * @author Florent
 */
@Stateless
public class GroupDAOImpl extends GenericDAOImpl<Group, UUID> implements GroupDAO{
    
    public GroupDAOImpl() {
        super(Group.class);
    }
    
}
