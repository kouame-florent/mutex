/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.domain.dao.InodeDAO;

/**
 *
 * @author Florent
 */
@Stateless
public class MutextFileService {
    
    @Inject InodeDAO mutexFileDAO;
    
    public Result<Inode> get(VirtualPage dto){
        return mutexFileDAO
                .findById(UUID.fromString(dto.getInodeUUID()));
    }
}
