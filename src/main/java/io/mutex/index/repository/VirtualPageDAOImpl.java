/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.repository;

import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.index.entity.Inode;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.shared.repository.GenericDAOImpl;

/**
 *
 * @author Florent
 */
@Stateless
public class VirtualPageDAOImpl extends GenericDAOImpl<VirtualPage, String>
        implements VirtualPageDAO {

    public VirtualPageDAOImpl() {
        super(VirtualPage.class);
    }

    @Override
    public List<VirtualPage> findByFile(Inode mutexFile) {
        TypedQuery<VirtualPage> query
                = em.createNamedQuery("VirtualPage.findByMutexFile",
                        VirtualPage.class);
        query.setParameter("mutexFile", mutexFile);

        return query.getResultList();

    }

}
