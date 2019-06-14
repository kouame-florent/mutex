/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.entity.InodeGroup;

/**
 *
 * @author Florent
 */
public interface InodeGroupDAO extends GenericDAO<InodeGroup, String>{
     public Result<InodeGroup> findByGroupAndHash(@NotNull Group ownerGroup, @NotNull String fileHash);
     public Result<InodeGroup> findByGroup(@NotNull Group group);
     public Result<InodeGroup> findByInode(@NotNull Inode inode);
}
