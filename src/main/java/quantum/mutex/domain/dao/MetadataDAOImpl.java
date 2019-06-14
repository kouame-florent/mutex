/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import javax.ejb.Stateless;

/**
 *
 * @author Florent
 */
@Stateless
public class MetadataDAOImpl {
    
//    public MetadataDAOImpl() {
//        super(MetadataDTO.class);
//    }
//
//    @Override
//    public Result<MetadataDTO> findByAttributeName(String attributeName) {
//        TypedQuery<MetadataDTO> query = em.createNamedQuery("Metadata.findByAttributeName", entityClass);
//        query.setParameter("attributeName",attributeName);
//        List<MetadataDTO> results = query.getResultList();
//        
//        return !results.isEmpty() ? Result.of(results.get(0)) : Result.empty();
//    }
//
//    @Override
//    public Result<MetadataDTO> findByAttributeNameAndAttributeValue(String attributeName, String attributeValue) {
//        TypedQuery<MetadataDTO> query = em.createNamedQuery("Metadata.findByAttributeNameAndAttributeValue", entityClass);
//        query.setParameter("attributeName",attributeName);
//        query.setParameter("attributeValue", attributeValue);
//        List<MetadataDTO> results = query.getResultList();
//        
//        return !results.isEmpty() ? Result.of(results.get(0)) : Result.empty();
//    }
    
}
