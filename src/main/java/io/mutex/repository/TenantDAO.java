/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.repository;


import io.mutex.repository.GenericDAO;
import java.util.Optional;
import io.mutex.domain.entity.Tenant;


/**
 *
 * @author Florent
 */
public interface TenantDAO extends GenericDAO<Tenant, String> {
    Optional<Tenant> findByName(String name);
    
}
