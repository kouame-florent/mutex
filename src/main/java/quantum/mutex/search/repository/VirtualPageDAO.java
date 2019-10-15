/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.search.repository;

import quantum.mutex.shared.repository.GenericDAO;
import java.util.List;
import quantum.mutex.index.domain.entity.Inode;
import quantum.mutex.search.valueobject.VirtualPage;

/**
 *
 * @author Florent
 */
public interface VirtualPageDAO extends GenericDAO<VirtualPage, String>{
    List<VirtualPage> findByFile(Inode mutexFile);
}
