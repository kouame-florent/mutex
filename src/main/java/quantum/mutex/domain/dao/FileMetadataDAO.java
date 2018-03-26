/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.Optional;
import java.util.UUID;
import quantum.mutex.domain.FileMetadata;

/**
 *
 * @author Florent
 */
public interface FileMetadataDAO extends GenericDAO<FileMetadata, UUID>{
    
    Optional<FileMetadata> findByAttributeName(String attributeName);
    Optional<FileMetadata> findByAttributeNameAndAttributeValue(String attributeName,String attributeValue);
}
