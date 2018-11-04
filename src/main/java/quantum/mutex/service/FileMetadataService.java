/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.constraints.NotNull;
import quantum.mutex.common.Result;
import quantum.mutex.common.Tuple;
import quantum.mutex.domain.MutexFile;
import quantum.mutex.dto.MetadataDTO;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.MetadataDAO;
import quantum.mutex.domain.dao.MutexFileDAO;
import quantum.mutex.service.elastic.IndexingService;
import quantum.mutex.service.elastic.MappingService;


/**
 *
 * @author Florent
 */
@Stateless
public class FileMetadataService {

    private static final Logger LOG = Logger.getLogger(FileMetadataService.class.getName());

    @Inject MutexFileDAO documentDAO;
    @Inject IndexingService indexingService;
   
    
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
