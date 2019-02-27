/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.primefaces.model.UploadedFile;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;

/**
 *
 * @author Florent
 */
@Stateless
public class FileInfoService {
    
    @Inject EncryptionService encryptionService;
    
    public Result<FileInfo> newFileInfo(){
        return Result.of(new FileInfo());
    }
    
    public Result<FileInfo> newFileInfo(UploadedFile uploadedFile){
        var fi = new FileInfo();
        fi.setFileName(uploadedFile.getFileName());
        fi.setFileSize(uploadedFile.getSize());
        
        return Result.of(fi);
    }
    
     public Result<FileInfo> newFileInfo(ArchiveEntry archiveEntry){
        var fi = new FileInfo();
        fi.setFileName(archiveEntry.getName());
        fi.setFileSize(archiveEntry.getSize());
        
        return Result.of(fi);
    }
    
    public Result<String> buildHash(Path path){
        try{
            return Result.success(EncryptionService.hash(Files.newInputStream(path)));
        }catch(IOException ex){
            return Result.failure(ex);
        }
    }
}
