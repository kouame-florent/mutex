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
import quantum.mutex.domain.Metadata;

/**
 *
 * @author Florent
 */
@Stateless
public class MetadataDAOImpl extends GenericDAOImpl<Metadata, UUID> 
        implements MetadataDAO{
    
    public MetadataDAOImpl() {
        super(Metadata.class);
    }

    @Override
    public Optional<Metadata> findByAttributeName(String attributeName) {
        TypedQuery<Metadata> query = em.createNamedQuery("Metadata.findByAttributeName", entityClass);
        query.setParameter("attributeName",attributeName);
        List<Metadata> results = query.getResultList();
        
        return !results.isEmpty() ? Optional.of(results.get(0)) : Optional.empty();
    }

    @Override
    public Optional<Metadata> findByAttributeNameAndAttributeValue(String attributeName, String attributeValue) {
        TypedQuery<Metadata> query = em.createNamedQuery("Metadata.findByAttributeNameAndAttributeValue", entityClass);
        query.setParameter("attributeName",attributeName);
        query.setParameter("attributeValue", attributeValue);
        List<Metadata> results = query.getResultList();
        
        return !results.isEmpty() ? Optional.of(results.get(0)) : Optional.empty();
    }
    
}
