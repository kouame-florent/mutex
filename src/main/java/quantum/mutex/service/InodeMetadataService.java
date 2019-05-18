/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.time.LocalDateTime;
import java.util.Map;
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
    
//    public Metadata addBaseProperties(@NotNull Metadata meta,@NotNull FileInfo fileInfo){
//        meta.setInodeUUID(fileInfo.getInode().getUuid().toString());
//        meta.setInodeHash(fileInfo.getFileHash());
//        meta.setFileName(fileInfo.getFileName());
//        meta.setFileOwner(environmentUtils.getUserlogin());
//        meta.setFileGroup(fileInfo.getGroup().getName());
//        meta.setFileTenant(environmentUtils.getUserTenantName());
//        meta.setFileSize(fileInfo.getFileSize());
//        meta.setFileCreated(LocalDateTime.now());
//        
//        return meta;
//    }
//    
//    public Metadata addMetadata(@NotNull Metadata meta,@NotNull Map<String,String> tikeMetadata){
//       meta.setContent(getMetadatasAsString(tikeMetadata));
//       return meta;
//    }
           
  
    
//    public Result<Metadata> addContentType(@NotNull Metadata meta,@NotNull Map<String,String> tikaMetadata){
//        return getContentType(tikaMetadata)
//                .map(t -> {meta.setFileMimeType(t);return meta;});
//    }
   
   
    
//    public Result<Metadata> addLanguage(@NotNull Metadata meta,@NotNull Map<String,String> tikaMetadata){
//        return getLanguage(tikaMetadata)
//                .map(t -> {meta.setFileMimeType(t);return meta;});
//    }
    
   
}
