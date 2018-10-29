/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.common.Result;
import quantum.mutex.domain.MutexFile;
import quantum.mutex.domain.dao.MutexFileDAO;
import quantum.mutex.dto.VirtualPageDTO;

/**
 *
 * @author Florent
 */
@Stateless
public class MutextFileService {
    
    @Inject MutexFileDAO mutexFileDAO;
    
    public Result<MutexFile> get(VirtualPageDTO dto){
        return mutexFileDAO
                .findById(UUID.fromString(dto.getMutexFileUUID()));
    }
}
