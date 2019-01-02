/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.service.UserGroupService;
import quantum.mutex.service.api.ElasticIndexingService;
import quantum.mutex.domain.dao.InodeDAO;


/**
 *
 * @author Florent
 */
@Stateless
public class FileMetadataService {

    private static final Logger LOG = Logger.getLogger(FileMetadataService.class.getName());

    @Inject InodeDAO documentDAO;
    @Inject ElasticIndexingService indexingService;
    @Inject UserGroupService userGroupService;
    
    public Result<FileInfo> index(FileInfo fileInfo){
        fileInfo.getFileMetadatas().forEach(meta -> {  
            LOG.log(Level.INFO, "---> CURRENT META: {0}", meta.getAttributeName());
            meta.setMutexFileUUID(fileInfo.getInode().getUuid().toString());
            meta.setMutexFileHash(fileInfo.getFileHash());
            indexMetadatas(fileInfo, meta);
       });
        
        return Result.of(fileInfo);
    }
    
    private void indexMetadatas(FileInfo fileInfo,Metadata meta){
         indexingService.indexMetadata(fileInfo.getGroup(),meta);
    }
  
}
