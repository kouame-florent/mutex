/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.repository;

import java.util.List;
import io.mutex.index.entity.Inode;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.shared.repository.GenericDAO;

/**
 *
 * @author Florent
 */
public interface VirtualPageDAO extends GenericDAO<VirtualPage, String>{
    List<VirtualPage> findByFile(Inode mutexFile);
}
