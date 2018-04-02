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
import quantum.mutex.domain.DocumentFile;
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

    @Override
    public List<VirtualPage> findByDocument(DocumentFile document) {
        TypedQuery<VirtualPage> query = 
                em.createNamedQuery("VirtualPage.findByDocument",
                        VirtualPage.class);
        query.setParameter("document", document);
        
        List<VirtualPage> results; 
        
        results = query.getResultList();
       
        return results;
    }
    
}
