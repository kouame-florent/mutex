/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.search.repository;

import mutex.shared.repository.GenericDAO;
import java.util.List;
import mutex.index.domain.entity.Inode;
import mutex.search.valueobject.VirtualPage;

/**
 *
 * @author Florent
 */
public interface VirtualPageDAO extends GenericDAO<VirtualPage, String>{
    List<VirtualPage> findByFile(Inode mutexFile);
}
