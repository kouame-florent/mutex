/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.AdminUser;
import quantum.mutex.domain.entity.Tenant;


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
    public Result<AdminUser> findByLogin(String login) {
        TypedQuery<AdminUser> query = 
               em.createNamedQuery("AdminUser.findByLogin", AdminUser.class);
        query.setParameter("login", login);
       
        List<AdminUser> results =  query.getResultList();
        if(!results.isEmpty()){
            return Result.of(results.get(0));
        }
        
        return Result.empty();
    }

    @Override
    public List<AdminUser> findByTenant(Tenant tenant) {
        TypedQuery<AdminUser> query = 
               em.createNamedQuery("AdminUser.findByTenant", AdminUser.class);
        query.setParameter("tenant", tenant);
       
        return query.getResultList();
    }

    @Override
    public List<AdminUser> findNotAssignedToTenant() {
        TypedQuery<AdminUser> query = 
               em.createNamedQuery("AdminUser.findNotAssignedToTenant", AdminUser.class);
       
        return query.getResultList();
    }
     
}
