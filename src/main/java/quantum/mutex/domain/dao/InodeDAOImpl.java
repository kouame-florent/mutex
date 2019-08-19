/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;


import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.util.functional.Result;

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
    public Result<Inode> findByHash(String fileHash) {
        TypedQuery<Inode> query = 
               em.createNamedQuery("Inode.findByHash", Inode.class);
//        query.setParameter("ownerUser", ownerUser);
//        query.setParameter("ownerGroup", ownerGroup);  
        query.setParameter("fileHash", fileHash);  
       
        return query.getResultList().isEmpty() ? Result.empty() : 
                Result.success(query.getResultList().get(0));

    }
    
}
