/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Role;
import quantum.mutex.domain.entity.RoleName;

/**
 *
 * @author Florent
 */
@Stateless
public class RoleDAOImpl extends GenericDAOImpl<Role, UUID> implements RoleDAO{
    
    public RoleDAOImpl() {
        super(Role.class);
    }

    @Override
    public Result<Role> findByName(RoleName name) {
        TypedQuery<Role> query = 
               em.createNamedQuery("Role.findByName", Role.class);
        query.setParameter("name", name.value());
       
        List<Role> results =  query.getResultList();
        if(!results.isEmpty()){
            return Result.of(results.get(0));
        }
        
        return Result.empty();
    }
    
}
