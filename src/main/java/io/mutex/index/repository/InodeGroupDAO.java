/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.repository;



import java.util.Optional;
import io.mutex.user.entity.Group;
import io.mutex.index.entity.Inode;
import io.mutex.index.entity.InodeGroup;
import io.mutex.shared.repository.GenericDAO;


/**
 *
 * @author Florent
 */
public interface InodeGroupDAO extends GenericDAO<InodeGroup, String>{
     public Optional<InodeGroup> findByGroupAndHash( Group ownerGroup,  String fileHash);
     public Optional<InodeGroup> findByGroup( Group group);
     public Optional<InodeGroup> findByInode( Inode inode);
}