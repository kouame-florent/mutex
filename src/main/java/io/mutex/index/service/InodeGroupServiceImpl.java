/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.entity.Inode;
import io.mutex.index.entity.InodeGroup;
import io.mutex.index.repository.InodeGroupDAO;
import io.mutex.user.entity.Group;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 *
 * @author florent
 */
@Stateless
public class InodeGroupServiceImpl implements InodeGroupService{
    
    @Inject InodeGroupDAO inodeGroupDAO;

    @Override
    public Optional<InodeGroup> create(@NotNull Inode inode, @NotNull Group group) {
        if(inodeGroupDAO.getByGroupAndInode(group,inode).isEmpty()){
            return Optional.of(new InodeGroup(inode, group));
        }
        return Optional.empty();
        
    }

    @Override
    public Optional<InodeGroup> getByInodeAndGroup(@NotNull Inode inode,@NotNull Group group) {
        return inodeGroupDAO.getByGroupAndInode(group,inode);
    }

    @Override
    public List<InodeGroup> getAll() {
        return inodeGroupDAO.findAll();
    }

    @Override
    public void delete(@NotNull Inode inode,@NotNull Group group) {
       Optional<InodeGroup> oIg = inodeGroupDAO.getByGroupAndInode(group, inode);
       oIg.ifPresent(ig -> inodeGroupDAO.makeTransient(ig));
       
    }
    
}
