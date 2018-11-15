/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.Optional;
import java.util.UUID;
import quantum.functional.api.Result;
import quantum.mutex.domain.Tenant;

/**
 *
 * @author Florent
 */
public interface TenantDAO extends GenericDAO<Tenant, UUID> {
    Result<Tenant> findByName(String name);
    
}
