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
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 *
 * @author Florent
 */
public interface InodeGroupDAO extends GenericDAO<InodeGroup, String>{
     public Optional<InodeGroup> getByGroupAndHash(@NotNull Group ownerGroup, @NotBlank String fileHash);
     public Optional<InodeGroup> getByGroupAndInode(@NotNull Group group,@NotNull Inode inode);
     public List<InodeGroup> getByGroup(@NotNull Group group);
     public List<InodeGroup> getByInode(@NotNull Inode inode);
}
