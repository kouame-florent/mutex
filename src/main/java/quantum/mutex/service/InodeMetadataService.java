/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.service.search.DocumentService;
import quantum.mutex.util.EnvironmentUtils;


/**
 *
 * @author Florent
 */
@Stateless
public class InodeMetadataService {

    private static final Logger LOG = Logger.getLogger(InodeMetadataService.class.getName());

    @Inject DocumentService documentService;
    @Inject EnvironmentUtils environmentUtils;
    
    public List<Metadata> buildMetadatas(@NotNull FileInfo fileInfo){
        return fileInfo.getFileMetadatas().stream()
                .map(m -> addProperties(m, fileInfo))
                .collect(Collectors.toList());
    }
  
    
    private Metadata addProperties(@NotNull Metadata meta,@NotNull FileInfo fileInfo){
//        LOG.log(Level.INFO, "---> CURRENT META: {0}", meta.getAttributeName());
        meta.setInodeUUID(fileInfo.getInode().getUuid().toString());
        meta.setInodeHash(fileInfo.getFileHash());
        meta.setFileName(fileInfo.getFileName());
        meta.setFileOwner(environmentUtils.getUserlogin());
        meta.setFileGroup(fileInfo.getGroup().getName());
        meta.setFileTenant(environmentUtils.getUserTenantName());
        meta.setFileSize(fileInfo.getFileSize());
        meta.setFileCreated(LocalDateTime.now());
        
        return meta;
    }
}
