/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.search.repository;


import mutex.shared.repository.GenericDAO;
import mutex.search.valueobject.Metadata;

/**
 *
 * @author Florent
 */
public interface MetadataDAO extends GenericDAO<Metadata, String>{
    
//    Optional<MetadataDTO> findByAttributeName(String attributeName);
//    Optional<MetadataDTO> findByAttributeNameAndAttributeValue(String attributeName,String attributeValue);
}
