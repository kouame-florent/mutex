/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.search.repository;


import quantum.mutex.shared.repository.GenericDAO;
import quantum.mutex.search.valueobject.Metadata;

/**
 *
 * @author Florent
 */
public interface MetadataDAO extends GenericDAO<Metadata, String>{
    
//    Optional<MetadataDTO> findByAttributeName(String attributeName);
//    Optional<MetadataDTO> findByAttributeNameAndAttributeValue(String attributeName,String attributeValue);
}
