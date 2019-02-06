/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Inode;


/**
 *
 * @author Florent
 */
public interface InodeDAO extends GenericDAO<Inode, UUID>{
    public Result<Inode> findByHash(String fileHash);
}