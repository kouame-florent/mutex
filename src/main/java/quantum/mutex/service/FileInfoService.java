/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.primefaces.model.UploadedFile;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.entity.Inode;

/**
 *
 * @author Florent
 */
@Stateless
public class FileInfoService {
    
    @Inject EncryptionService encryptionService;
    
//    public Result<FileInfo> newFileInfo(){
//        return Result.of(new FileInfo());
//    }
//    
//    public Result<FileInfo> newFileInfo(UploadedFile uploadedFile){
//        var fi = new FileInfo();
//        fi.setFileName(uploadedFile.getFileName());
//        fi.setFileSize(uploadedFile.getSize());
//        
//        return Result.of(fi);
//    }
    
//    public Result<FileInfo> newFileInfo(ArchiveEntry archiveEntry){
//        var fi = new FileInfo();
//        fi.setFileName(archiveEntry.getName());
//        fi.setFileSize(archiveEntry.getSize());
//        
//        return Result.of(fi);
//    }
    
//    public Result<FileInfo> addMetadata(FileInfo fileInfo,Map<String,String> tikaMetas){
//        Metadata meta = toMutexMetadata(tikaMetas);
//        fileInfo.setFileMetadata(meta);
//        return Result.of(fileInfo);
//    }
    
//    public Result<FileInfo> addRawContent(FileInfo fileInfo,String rawContent){
//        fileInfo.setRawContent(rawContent);
//        return Result.of(fileInfo);
//    }
//    
    public Metadata toMutexMetadata(Map<String,String> map){
       Metadata meta = new Metadata();
       meta.setContent(metadataContent(map));
       return meta;
    }
    
    private String metadataContent(Map<String,String> map){
        return map.entrySet().stream().filter(e -> !e.getKey().equals("X-Parsed-By"))
                .map(e -> e.getKey() + ": " + e.getValue() )
                .collect(Collectors.joining(";"));
    }
    
//    public Result<FileInfo> addInode(FileInfo fileInfo,Inode inode){
//        fileInfo.setInode(inode);
//        return Result.of(fileInfo);
//    }
// 
    
//    public Result<String> buildHash(Path path){
//        try{
//            return Result.success(EncryptionService.hash(Files.newInputStream(path)));
//        }catch(IOException ex){
//            return Result.failure(ex);
//        }
//    }
}
