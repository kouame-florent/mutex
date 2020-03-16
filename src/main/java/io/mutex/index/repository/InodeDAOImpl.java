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
import io.mutex.user.entity.Searcher;
import java.util.List;
import static java.util.stream.Collectors.toList;


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
    public List<Inode> findByOwner(Searcher owner) {
        TypedQuery<Inode> query = 
               em.createNamedQuery("Inode.findByOwner", Inode.class);
        query.setParameter("owner", owner);  
       
        return query.getResultStream().collect(toList());
    }
    
}
