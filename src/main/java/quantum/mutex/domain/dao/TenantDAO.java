/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;


import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.util.functional.Result;

/**
 *
 * @author Florent
 */
public interface TenantDAO extends GenericDAO<Tenant, String> {
    Result<Tenant> findByName(String name);
    
}
