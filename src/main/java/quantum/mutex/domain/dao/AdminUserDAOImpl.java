/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
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
    public Optional<AdminUser> findByLogin(String login) {
        TypedQuery<AdminUser> query = 
               em.createNamedQuery("AdminUser.findByLogin", AdminUser.class);
        query.setParameter("login", login);
       
        List<AdminUser> Optionals =  query.getResultList();
        if(!Optionals.isEmpty()){
            return Optional.of(Optionals.get(0));
        }
        
        return Optional.empty();
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
