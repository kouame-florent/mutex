/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.repository;


import io.mutex.domain.valueobject.Metadata;
import io.mutex.repository.GenericDAO;

/**
 *
 * @author Florent
 */
public interface MetadataDAO extends GenericDAO<Metadata, String>{
    
//    Optional<MetadataDTO> findByAttributeName(String attributeName);
//    Optional<MetadataDTO> findByAttributeNameAndAttributeValue(String attributeName,String attributeValue);
}
