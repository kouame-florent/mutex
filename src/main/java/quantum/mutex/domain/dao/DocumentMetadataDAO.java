/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.Optional;
import java.util.UUID;
import quantum.mutex.domain.DocumentMetadata;

/**
 *
 * @author Florent
 */
public interface DocumentMetadataDAO extends GenericDAO<DocumentMetadata, UUID>{
    
    Optional<DocumentMetadata> findByAttributeName(String attributeName);
    Optional<DocumentMetadata> findByAttributeNameAndAttributeValue(String attributeName,String attributeValue);
}
