/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.repository;

import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.user.entity.Group;
import io.mutex.index.entity.Inode;
import io.mutex.index.entity.InodeGroup;
import io.mutex.shared.repository.GenericDAOImpl;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


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
    public Optional<InodeGroup> findByGroupAndHash(@NotNull Group group,@NotBlank String fileHash) {
        TypedQuery<InodeGroup> query = 
               em.createNamedQuery("InodeGroup.findByGroupAndHash", InodeGroup.class);
        query.setParameter("group", group);  
        query.setParameter("fileHash", fileHash);  
       
        return query.getResultStream().findFirst();
    }
    
    @Override
    public Optional<InodeGroup> findByGroupAndInode(@NotNull Group group,@NotNull Inode inode) {
        
        TypedQuery<InodeGroup> query = 
               em.createNamedQuery("InodeGroup.findByGroupAndInode", InodeGroup.class);
        query.setParameter("group", group);  
        query.setParameter("inode", inode);  
       
        return query.getResultStream().findFirst();
        
    }


    @Override
    public List<InodeGroup> findByGroup(@NotNull Group group) {
        TypedQuery<InodeGroup> query = 
               em.createNamedQuery("InodeGroup.findByGroup", InodeGroup.class);
        query.setParameter("group", group);  
        return query.getResultList();
    }

    @Override
    public List<InodeGroup> findByInode(@NotNull Inode inode) {
        TypedQuery<InodeGroup> query = 
               em.createNamedQuery("InodeGroup.findByInode", InodeGroup.class);
        query.setParameter("inode", inode);  
        return query.getResultList();
    }

    
}
