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
import quantum.mutex.domain.FileMetadata;

/**
 *
 * @author Florent
 */
@Stateless
public class FileMetadataDAOImpl extends GenericDAOImpl<FileMetadata, UUID> 
        implements FileMetadataDAO{
    
    public FileMetadataDAOImpl() {
        super(FileMetadata.class);
    }

    @Override
    public Optional<FileMetadata> findByAttributeName(String attributeName) {
        TypedQuery<FileMetadata> query = em.createNamedQuery("FileMetadata.findByAttributeName", entityClass);
        query.setParameter("attributeName",attributeName);
        List<FileMetadata> results = query.getResultList();
        
        return !results.isEmpty() ? Optional.of(results.get(0)) : Optional.empty();
    }

    @Override
    public Optional<FileMetadata> findByAttributeNameAndAttributeValue(String attributeName, String attributeValue) {
        TypedQuery<FileMetadata> query = em.createNamedQuery("FileMetadata.findByAttributeNameAndAttributeValue", entityClass);
        query.setParameter("attributeName",attributeName);
        query.setParameter("attributeValue", attributeValue);
        List<FileMetadata> results = query.getResultList();
        
        return !results.isEmpty() ? Optional.of(results.get(0)) : Optional.empty();
    }
    
}
