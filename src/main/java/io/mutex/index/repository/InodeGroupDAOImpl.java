/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.repository;

import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.domain.entity.Group;
import io.mutex.domain.entity.Inode;
import io.mutex.domain.entity.InodeGroup;
import io.mutex.repository.GenericDAOImpl;


/**
 *
 * @author Florent
 */
@Stateless
public class InodeGroupDAOImpl extends GenericDAOImpl<InodeGroup, String> implements InodeGroupDAO{

    public InodeGroupDAOImpl() {
        super(InodeGroup.class);
    }
    
    @Override
    public Optional<InodeGroup> findByGroupAndHash(Group group, String fileHash) {
        TypedQuery<InodeGroup> query = 
               em.createNamedQuery("InodeGroup.findByGroupAndHash", InodeGroup.class);
        query.setParameter("group", group);  
        query.setParameter("fileHash", fileHash);  
       
        return query.getResultList().isEmpty() ? Optional.empty() : 
                Optional.ofNullable(query.getResultList().get(0));
    }

    @Override
    public Optional<InodeGroup> findByGroup(Group group) {
        TypedQuery<InodeGroup> query = 
               em.createNamedQuery("InodeGroup.findByGroup", InodeGroup.class);
        query.setParameter("group", group);  
        return query.getResultList().isEmpty() ? Optional.empty() : 
                Optional.ofNullable(query.getResultList().get(0));
    }

    @Override
    public Optional<InodeGroup> findByInode(Inode inode) {
        TypedQuery<InodeGroup> query = 
               em.createNamedQuery("InodeGroup.findByInode", InodeGroup.class);
        query.setParameter("inode", inode);  
        return query.getResultList().isEmpty() ? Optional.empty() : 
               Optional.ofNullable(query.getResultList().get(0));
    }

}
