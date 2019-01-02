/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.MutexFile;
import quantum.mutex.domain.entity.User;

/**
 *
 * @author Florent
 */
public interface MutexFileDAO extends GenericDAO<MutexFile, UUID>{
    public Result<MutexFile> findByUserAndGroupAndHash(User ownerUser, Group ownerGroup, String fileHash);
}
