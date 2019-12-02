/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.index.repository;


import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import mutex.shared.repository.GenericDAOImpl;
import mutex.index.domain.entity.Inode;


/**
 *
 * @author Florent
 */
@Stateless
public class InodeDAOImpl extends GenericDAOImpl<Inode, String> implements InodeDAO{
    
    public InodeDAOImpl() {
        super(Inode.class);
    }

    @Override
    public Optional<Inode> findByHash(String fileHash) {
        TypedQuery<Inode> query = 
               em.createNamedQuery("Inode.findByHash", Inode.class);
//        query.setParameter("ownerUser", ownerUser);
//        query.setParameter("ownerGroup", ownerGroup);  
        query.setParameter("fileHash", fileHash);  
       
        return query.getResultList().isEmpty() ? Optional.empty() : 
                Optional.ofNullable(query.getResultList().get(0));

    }
    
}
