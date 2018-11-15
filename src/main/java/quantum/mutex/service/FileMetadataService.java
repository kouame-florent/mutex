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

import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.MutexFileDAO;
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
   
    
    public Result<FileInfoDTO> index(@NotNull FileInfoDTO fileInfoDTO){
        fileInfoDTO.getFileMetadatas().forEach(meta -> {  
            LOG.log(Level.INFO, "---> CURRENT META: {0}", meta.getAttributeName());
            meta.setMutexFileUUID(fileInfoDTO.getFile().getUuid().toString());
            indexingService.indexingMetadata(fileInfoDTO.getFile().getOwnerGroup(), meta);

        });
        
        return Result.of(fileInfoDTO);
    }
    
//    Function<Filem>
    
}
