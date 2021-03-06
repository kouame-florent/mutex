/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.repository;


import java.util.Optional;
import io.mutex.index.entity.Inode;
import io.mutex.shared.repository.GenericDAO;
import io.mutex.user.entity.Searcher;
import java.util.List;



/**
 *
 * @author Florent
 */
public interface InodeDAO extends GenericDAO<Inode, String>{
    public Optional<Inode> findByHash(String fileHash);
    public List<Inode> findByOwner(Searcher owner);
}
