/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.repository;


import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.index.entity.Inode;
import io.mutex.shared.repository.GenericDAOImpl;
import io.mutex.user.entity.StandardUser;


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
        query.setParameter("fileHash", fileHash);  
       
        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<Inode> findByOwnerUser(StandardUser ownerUser) {
        TypedQuery<Inode> query = 
               em.createNamedQuery("Inode.findByOwnerUser", Inode.class);
        query.setParameter("ownerUser", ownerUser);  
       
        return query.getResultStream().findFirst();
    }
    
}
