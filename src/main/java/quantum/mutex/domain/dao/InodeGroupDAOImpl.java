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
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.entity.InodeGroup;

/**
 *
 * @author Florent
 */
@Stateless
public class InodeGroupDAOImpl extends GenericDAOImpl<InodeGroup, UUID> implements InodeGroupDAO{

    public InodeGroupDAOImpl() {
        super(InodeGroup.class);
    }
    
    @Override
    public Result<InodeGroup> findByGroupAndHash(Group group, String fileHash) {
        TypedQuery<InodeGroup> query = 
               em.createNamedQuery("InodeGroup.findByGroupAndHash", InodeGroup.class);
//        query.setParameter("ownerUser", ownerUser);
        query.setParameter("group", group);  
        query.setParameter("fileHash", fileHash);  
       
        return query.getResultList().isEmpty() ? Result.empty() : 
                Result.success(query.getResultList().get(0));

    }

}
