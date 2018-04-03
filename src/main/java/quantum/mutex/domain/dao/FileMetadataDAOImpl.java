/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import javax.ejb.Stateless;
import quantum.mutex.domain.FileMetadata;

/**
 *
 * @author Florent
 */
@Stateless
public class FileMetadataDAOImpl extends GenericDAOImpl<FileMetadata, FileMetadata.Id> 
        implements FileMetadataDAO{
    
    public FileMetadataDAOImpl() {
        super(FileMetadata.class);
    }
    
}
