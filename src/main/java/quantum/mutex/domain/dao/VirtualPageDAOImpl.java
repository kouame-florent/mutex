/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.mutex.domain.entity.MutexFile;
import quantum.mutex.domain.dto.VirtualPageDTO;

/**
 *
 * @author Florent
 */
@Stateless
public class VirtualPageDAOImpl extends GenericDAOImpl<VirtualPageDTO, UUID> 
        implements VirtualPageDAO{
    
    public VirtualPageDAOImpl() {
        super(VirtualPageDTO.class);
    }

    @Override
    public List<VirtualPageDTO> findByFile(MutexFile mutexFile) {
        TypedQuery<VirtualPageDTO> query = 
                em.createNamedQuery("VirtualPage.findByMutexFile",
                        VirtualPageDTO.class);
        query.setParameter("mutexFile", mutexFile);
        
        List<VirtualPageDTO> results; 
        
        results = query.getResultList();
       
        return results;
    }
    
}
