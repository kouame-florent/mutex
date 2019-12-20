/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;


import io.mutex.shared.repository.GenericDAO;
import java.util.Optional;
import io.mutex.user.entity.Tenant;
import io.mutex.shared.repository.GenericDAO;


/**
 *
 * @author Florent
 */
public interface TenantDAO extends GenericDAO<Tenant, String> {
    Optional<Tenant> findByName(String name);
    
}