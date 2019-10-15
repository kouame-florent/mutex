/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.index.repository;


import java.util.Optional;
import quantum.mutex.shared.repository.GenericDAO;
import quantum.mutex.index.domain.entity.Inode;



/**
 *
 * @author Florent
 */
public interface InodeDAO extends GenericDAO<Inode, String>{
    public Optional<Inode> findByHash(String fileHash);
}
