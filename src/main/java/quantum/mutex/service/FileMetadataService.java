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
import javax.validation.constraints.NotNull;
import quantum.functional.api.Result;

import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dao.MutexFileDAO;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.service.GroupService;
import quantum.mutex.service.api.ElasticIndexingService;


/**
 *
 * @author Florent
 */
@Stateless
public class FileMetadataService {

    private static final Logger LOG = Logger.getLogger(FileMetadataService.class.getName());

    @Inject MutexFileDAO documentDAO;
    @Inject ElasticIndexingService indexingService;
    @Inject GroupService groupService;
    
    public Result<FileInfo> index(@NotNull FileInfo fileInfoDTO){
        fileInfoDTO.getFileMetadatas().forEach(meta -> {  
            LOG.log(Level.INFO, "---> CURRENT META: {0}", meta.getAttributeName());
            meta.setMutexFileUUID(fileInfoDTO.getFile().getUuid().toString());
            meta.setMutexFileHash(fileInfoDTO.getFileHash());
            indexMetadatas(fileInfoDTO, meta);
       });
        
        return Result.of(fileInfoDTO);
    }
    
    private void indexMetadatas(FileInfo fileInfoDTO,Metadata meta){
       groupService.retrieveGroups(fileInfoDTO.getFile().getOwnerUser())
               .forEach(g -> indexingService.indexMetadata(g,meta));
    }
  
}
