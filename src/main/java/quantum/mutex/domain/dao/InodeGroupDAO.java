/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;



import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.entity.InodeGroup;
import quantum.mutex.util.functional.Result;

/**
 *
 * @author Florent
 */
public interface InodeGroupDAO extends GenericDAO<InodeGroup, String>{
     public Result<InodeGroup> findByGroupAndHash( Group ownerGroup,  String fileHash);
     public Result<InodeGroup> findByGroup( Group group);
     public Result<InodeGroup> findByInode( Inode inode);
}
