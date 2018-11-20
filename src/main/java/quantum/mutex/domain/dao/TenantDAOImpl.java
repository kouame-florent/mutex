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
import quantum.mutex.domain.entity.Tenant;

/**
 *
 * @author Florent
 */
@Stateless
public class TenantDAOImpl extends GenericDAOImpl<Tenant, UUID> implements TenantDAO{
    
    public TenantDAOImpl() {
        super(Tenant.class);
    }

    @Override
    public Result<Tenant> findByName(String name) {
        
        TypedQuery<Tenant> query = 
               em.createNamedQuery("Tenant.findByName", Tenant.class);
        query.setParameter("name", name);
       
        List<Tenant> results =  query.getResultList();
        if(!results.isEmpty()){
            return Result.of(results.get(0));
        }
        
        return Result.empty();
   }
    
}
