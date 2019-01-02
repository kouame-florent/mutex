/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.io.Serializable;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.MutexFile;
import quantum.mutex.domain.entity.User;

/**
 *
 * @author Florent
 */
@Stateless
public class MutexFileDAOImpl extends GenericDAOImpl<MutexFile, UUID> implements MutexFileDAO{
    
    public MutexFileDAOImpl() {
        super(MutexFile.class);
    }

//    @Override
//    public Result<MutexFile> findByUserAndGroupAndHash(Group ownerGroup, String fileHash) {
//        TypedQuery<MutexFile> query = 
//               em.createNamedQuery("MutexFile.findByUserAndGroupAndHash", MutexFile.class);
////        query.setParameter("ownerUser", ownerUser);
//        query.setParameter("ownerGroup", ownerGroup);  
//        query.setParameter("fileHash", fileHash);  
//       
//        return query.getResultList().isEmpty() ? Result.empty() : 
//                Result.success(query.getResultList().get(0));
//
//    }
    
}
