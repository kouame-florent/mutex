/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import javax.ejb.Stateless;
import quantum.mutex.domain.VirtualPage;

/**
 *
 * @author Florent
 */
@Stateless
public class VirtualPageDAOImpl extends GenericDAOImpl<VirtualPage, UUID> 
        implements VirtualPageDAO{
    
    public VirtualPageDAOImpl() {
        super(VirtualPage.class);
    }
    
}
