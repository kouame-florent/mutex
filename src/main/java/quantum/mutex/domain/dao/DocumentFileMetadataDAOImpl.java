/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import javax.ejb.Stateless;
import quantum.mutex.domain.DocumentFileMetadata;

/**
 *
 * @author Florent
 */
@Stateless
public class DocumentFileMetadataDAOImpl extends GenericDAOImpl<DocumentFileMetadata, DocumentFileMetadata.Id> 
        implements DocumentFileMetadataDAO{
    
    public DocumentFileMetadataDAOImpl() {
        super(DocumentFileMetadata.class);
    }
    
}
