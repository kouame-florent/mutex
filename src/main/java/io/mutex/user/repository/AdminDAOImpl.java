/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import io.mutex.shared.repository.GenericDAOImpl;
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.shared.repository.GenericDAOImpl;



/**
 *
 * @author Florent
 */
@Stateless
public class AdminDAOImpl extends GenericDAOImpl<Admin, String> implements AdminDAO{
     
    public AdminDAOImpl() {
        super(Admin.class);
    }
    
    @Override
    public Optional<Admin> findByLogin(String login) {
        TypedQuery<Admin> query = 
               em.createNamedQuery("Admin.findByLogin", Admin.class);
        query.setParameter("login", login);
        return query.getResultStream().findFirst();
    }

//    @Override
//    public Optional<Admin> findBySpace(Space space) {
//        TypedQuery<Admin> query = 
//               em.createNamedQuery("Admin.findBySpace", Admin.class);
//        query.setParameter("space", space);
//       
//        return query.getResultList().stream().findFirst();
//    }

//    @Override
//    public List<Admin> findNotAssignedToSpace() {
//        TypedQuery<Admin> query = 
//               em.createNamedQuery("Admin.findNotAssignedToSpace", Admin.class);
//       
//        return query.getResultList();
//    }
//     
}
