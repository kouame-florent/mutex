/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.index.repository;



import java.util.Optional;
import quantum.mutex.shared.repository.GenericDAO;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.index.domain.entity.Inode;
import quantum.mutex.index.domain.entity.InodeGroup;


/**
 *
 * @author Florent
 */
public interface InodeGroupDAO extends GenericDAO<InodeGroup, String>{
     public Optional<InodeGroup> findByGroupAndHash( Group ownerGroup,  String fileHash);
     public Optional<InodeGroup> findByGroup( Group group);
     public Optional<InodeGroup> findByInode( Inode inode);
}
