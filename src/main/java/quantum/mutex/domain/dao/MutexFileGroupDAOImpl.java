/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.MutexFile;
import quantum.mutex.domain.entity.MutexFileGroup;

/**
 *
 * @author Florent
 */
@Stateless
public class MutexFileGroupDAOImpl extends GenericDAOImpl<MutexFileGroup, UUID> implements MutexFileGroupDAO{

    public MutexFileGroupDAOImpl() {
        super(MutexFileGroup.class);
    }
    
    @Override
    public Result<MutexFileGroup> findByGroupAndHash(Group group, String fileHash) {
        TypedQuery<MutexFileGroup> query = 
               em.createNamedQuery("MutexFileGroup.findByGroupAndHash", MutexFileGroup.class);
//        query.setParameter("ownerUser", ownerUser);
        query.setParameter("group", group);  
        query.setParameter("fileHash", fileHash);  
       
        return query.getResultList().isEmpty() ? Result.empty() : 
                Result.success(query.getResultList().get(0));

    }

}
