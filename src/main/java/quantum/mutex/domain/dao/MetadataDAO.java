/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.UUID;
import quantum.mutex.common.Result;
import quantum.mutex.dto.MetadataDTO;

/**
 *
 * @author Florent
 */
public interface MetadataDAO extends GenericDAO<MetadataDTO, UUID>{
    
//    Result<MetadataDTO> findByAttributeName(String attributeName);
//    Result<MetadataDTO> findByAttributeNameAndAttributeValue(String attributeName,String attributeValue);
}
