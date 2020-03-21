/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.entity.Inode;
import io.mutex.search.valueobject.FileInfo;
import io.mutex.user.entity.Searcher;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author florent
 */
public interface InodeService {
    
    Optional<Inode> create(FileInfo fileInfo, Map<String, String> meta);

    List<Inode> getByOwner(Searcher user);
    
    Optional<Inode> getByUUID(String inodeUUID);
    
}
