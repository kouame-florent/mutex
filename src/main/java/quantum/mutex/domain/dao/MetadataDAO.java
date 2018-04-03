/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.Optional;
import java.util.UUID;
import quantum.mutex.domain.Metadata;

/**
 *
 * @author Florent
 */
public interface MetadataDAO extends GenericDAO<Metadata, UUID>{
    
    Optional<Metadata> findByAttributeName(String attributeName);
    Optional<Metadata> findByAttributeNameAndAttributeValue(String attributeName,String attributeValue);
}
