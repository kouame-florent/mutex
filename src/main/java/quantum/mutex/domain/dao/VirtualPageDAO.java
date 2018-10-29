/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.UUID;
import quantum.mutex.domain.MutexFile;
import quantum.mutex.dto.VirtualPageDTO;

/**
 *
 * @author Florent
 */
public interface VirtualPageDAO extends GenericDAO<VirtualPageDTO, UUID>{
    List<VirtualPageDTO> findByFile(MutexFile mutexFile);
}
