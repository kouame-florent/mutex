/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.service.api.DocumentService;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.util.EnvironmentUtils;


/**
 *
 * @author Florent
 */
@Stateless
public class FileMetadataService {

    private static final Logger LOG = Logger.getLogger(FileMetadataService.class.getName());

    @Inject InodeDAO documentDAO;
    @Inject DocumentService indexingService;
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils environmentUtils;
    
    public Result<FileInfo> index(FileInfo fileInfo){
        LOG.log(Level.INFO, "--> FILE INFO METAS SIZE: {0}", fileInfo.getFileMetadatas().size());
        fileInfo.getFileMetadatas().forEach(meta -> {  
            LOG.log(Level.INFO, "---> CURRENT META: {0}", meta.getAttributeName());
            meta.setInodeUUID(fileInfo.getInode().getUuid().toString());
            meta.setInodeHash(fileInfo.getFileHash());
            meta.setFileName(fileInfo.getFileName());
            meta.setFileOwner(environmentUtils.getUserlogin());
            meta.setFileGroup(fileInfo.getGroup().getName());
            meta.setFileTenant(environmentUtils.getUserTenantName());
            meta.setFileSize(fileInfo.getFileSize());
            meta.setFileCreated(LocalDateTime.now());
            indexMetadatas(fileInfo, meta);
       });
        
        return Result.of(fileInfo);
    }
    
    private void indexMetadatas(FileInfo fileInfo,Metadata meta){
         indexingService.indexMetadata(fileInfo.getGroup(),meta);
    }
  
}
