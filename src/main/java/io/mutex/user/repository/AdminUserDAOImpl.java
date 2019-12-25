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
import io.mutex.user.entity.AdminUser;
import io.mutex.user.entity.Tenant;
import io.mutex.shared.repository.GenericDAOImpl;



/**
 *
 * @author Florent
 */
@Stateless
public class AdminUserDAOImpl extends GenericDAOImpl<AdminUser, String> implements AdminUserDAO{
     
    public AdminUserDAOImpl() {
        super(AdminUser.class);
    }
    
    @Override
    public Optional<AdminUser> findByLogin(String login) {
        TypedQuery<AdminUser> query = 
               em.createNamedQuery("AdminUser.findByLogin", AdminUser.class);
        query.setParameter("login", login);
        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<AdminUser> findByTenant(Tenant tenant) {
        TypedQuery<AdminUser> query = 
               em.createNamedQuery("AdminUser.findByTenant", AdminUser.class);
        query.setParameter("tenant", tenant);
       
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<AdminUser> findNotAssignedToTenant() {
        TypedQuery<AdminUser> query = 
               em.createNamedQuery("AdminUser.findNotAssignedToTenant", AdminUser.class);
       
        return query.getResultList();
    }
     
}
