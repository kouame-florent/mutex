/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import quantum.mutex.domain.DocumentMetadata;

/**
 *
 * @author Florent
 */
@Stateless
public class DocumentMetadataDAOImpl extends GenericDAOImpl<DocumentMetadata, UUID> 
        implements DocumentMetadataDAO{
    
    public DocumentMetadataDAOImpl() {
        super(DocumentMetadata.class);
    }

    @Override
    public Optional<DocumentMetadata> findByAttributeName(String attributeName) {
        TypedQuery<DocumentMetadata> query = em.createNamedQuery("DocumentMetadata.findByAttributeName", entityClass);
        query.setParameter("attributeName",attributeName);
        List<DocumentMetadata> results = query.getResultList();
        
        return !results.isEmpty() ? Optional.of(results.get(0)) : Optional.empty();
    }

    @Override
    public Optional<DocumentMetadata> findByAttributeNameAndAttributeValue(String attributeName, String attributeValue) {
        TypedQuery<DocumentMetadata> query = em.createNamedQuery("DocumentMetadata.findByAttributeNameAndAttributeValue", entityClass);
        query.setParameter("attributeName",attributeName);
        query.setParameter("attributeValue", attributeValue);
        List<DocumentMetadata> results = query.getResultList();
        
        return !results.isEmpty() ? Optional.of(results.get(0)) : Optional.empty();
    }
    
}
