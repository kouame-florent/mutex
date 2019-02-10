/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.util.Constants;
import quantum.mutex.util.EnvironmentUtils;

/**
 *
 * @author Florent
 */
@Stateless
public class FileIOService {

    private static final Logger LOG = Logger.getLogger(FileIOService.class.getName());
    
//    @Inject EncryptionService encryptionService;
    
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils environmentUtils;
    @Inject FileInfoService fileInfoService;
    
    public void createHomeDir(){
       
       if(Files.notExists(getHomeDir())){
           try {
               Files.createDirectories(getHomeDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
      
    }
    
    public void createSpoolDir(){
        
      if(Files.notExists(getSpoolDir())){
           try {
               Files.createDirectories(getSpoolDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
    }
    
    public void createStoreDir(){
     
       if(Files.notExists(getStoreDir())){
           try {
               Files.createDirectories(getStoreDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
    }
  
    public void createIndexDir(){
     
       if(Files.notExists(getIndexDir())){
           try {
               Files.createDirectories(getIndexDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
      
    }
    
    
    public Result<FileInfo> handle(UploadedFile uploadedFile,Group group){
//        List<Group> groups = environmentUtils.getUser().map(u -> userGroupService.getAllGroups(u))
//                .getOrElse(() -> Collections.EMPTY_LIST);
//        return groups.stream().map(g -> writeToSpool(uploadedFile, g))
//                .collect(Collectors.toList());
//        
//        return writeToSpool(uploadedFile, group);
          Result<Path> path = writeToSpool(uploadedFile, group);
          Result<String> hash = path.flatMap(p -> fileInfoService.buildHash(p));
          
          Result<FileInfo> fileInfo = fileInfoService.newFileInfo(uploadedFile);
          Result<FileInfo> withPath = path.flatMap(p -> fileInfo.map(fi -> {fi.setFilePath(p);return fi; }) );
          Result<FileInfo> withHash = hash.flatMap(h -> withPath.map(wp -> {wp.setFileHash(h); return wp; }) );
          Result<FileInfo> withGroup = withHash.map(wh -> {wh.setGroup(group); return wh;} );
          
          return withGroup;
                
       }
    
    public Result<Path> writeToSpool(@NotNull UploadedFile uploadedFile,Group group){

        Result<Path> path = createTempFilePath.apply(getSpoolDir().toString())
                .apply(provideUUID.get());
        
        Result<OutputStream> outStr = path.flatMap(p -> getOutput.apply(p));
        Result<InputStream> inStr = getInput.apply(uploadedFile);
        
        Result<Integer> res = inStr.map(in -> outStr.flatMap(ou -> this.copy.apply(in).apply(ou)))
                .getOrElse(() -> Result.failure("Error when creating file."));
        
        return res.isSuccess() ? path : Result.failure("Error when creating file.");
 
               
//        Result<Integer> res = inStr.map(in -> outStr.flatMap(ou -> this.copy.apply(in).apply(ou)))
//                .getOrElse(() -> Result.of(-1));
// 
//        Result<FileInfo> fileDTO = res.flatMap(r -> this.newFileInfo.apply(r))
//                .map(dto -> provideFileName.apply(dto).apply(uploadedFile.getFileName()))
//                .map(dto -> provideFileSize.apply(dto).apply(uploadedFile.getSize()))
//                .flatMap(dto -> path.map(p -> providePath.apply(p).apply(dto)))
//                .orElse(() -> Result.empty());
//         
//        Result<String> hashStr = path.flatMap(p -> hash.apply(p)).orElse(() -> Result.empty());
//
//        Result<FileInfo> fileInfoDTO = fileDTO
//                .map(d -> provideFileHash.apply(d)
//                        .apply(hashStr.getOrElse(() -> "")));
//        
//        Result<FileInfo> fileInfoWithGroup = 
//                fileInfoDTO.flatMap(fi -> provideGroup(fi, group));
                
//        return fileInfoWithGroup;
       
    }
    
////    private Result<FileInfo> buildFileInfo(@NotNull UploadedFile uploadedFile,Group group){
////         Result<FileInfo> fileDTO = res.flatMap(r -> this.newFileInfo.apply(r))
////                .map(dto -> provideFileName.apply(dto).apply(uploadedFile.getFileName()))
////                .map(dto -> provideFileSize.apply(dto).apply(uploadedFile.getSize()))
////                .flatMap(dto -> path.map(p -> providePath.apply(p).apply(dto)))
////                .orElse(() -> Result.empty());
////         
//////        Result<String> hashStr = path.flatMap(p -> hash.apply(p)).orElse(() -> Result.empty());
//////
//////        Result<FileInfo> fileInfoDTO = fileDTO
//////                .map(d -> provideFileHash.apply(d)
//////                        .apply(hashStr.getOrElse(() -> "")));
//////        
//////        Result<FileInfo> fileInfoWithGroup = 
//////                fileInfoDTO.flatMap(fi -> provideGroup(fi, group));
////    }
    
    private Result<FileInfo> provideGroup(FileInfo fileInfo,Group group){
        fileInfo.setGroup(group);
        return Result.of(fileInfo);
    }
    
    private final Function<String,Function<String,Result<Path>>> createTempFilePath = spoolDir 
            -> uuid -> Result.of(Paths.get(spoolDir, Paths.get(uuid).toString()));
    
    private final Supplier<String> provideUUID = () -> UUID.randomUUID().toString();
    
     
    private final Function<Path,Result<OutputStream>> getOutput = path -> {
        try{
            return Result.success(Files.newOutputStream(path, 
                    StandardOpenOption.CREATE_NEW));
        }catch(IOException ex){
            return Result.failure(ex);
        }
    };
    
    private final Function<UploadedFile,Result<InputStream>> getInput = upload -> {
        try{
            return Result.success(upload.getInputstream());
        }catch(IOException ex){
            return Result.failure(ex);
        }
    };
    
    private final Function<InputStream,Function<OutputStream,Result<Integer>>>  copy =  
        in -> out ->{
            try{
                   return  Result.success(IOUtils.copy(in, out));
            }catch(IOException ex){
                    return Result.failure(ex);
            }finally{
                try{
                    in.close();
                    out.close();
                }catch(IOException ex){
                    LOG.log(Level.SEVERE, "Error closing file: {0}", ex);
                }
            } 
        }; 

//    private final Function<Integer,Result<FileInfo>> newFileInfo = 
//        (res ) -> {return res > 0 ? Result.success(new FileInfo()) 
//                : Result.empty();
//        };
//    
//    private final Function<Path,Result<String>> hash = (path) -> {
//        try{
//            return Result.success(EncryptionService.hash(Files.newInputStream(path)));
//        }catch(IOException ex){
//            return Result.failure(ex);
//        }
//    };
     
//    private final Function<FileInfo,Function<String,FileInfo>> provideFileHash = 
//            fileInfo -> fhash ->{ fileInfo.setFileHash(fhash); return fileInfo;};
//    
//    private final Function<FileInfo,Function<String,FileInfo>> provideFileName = 
//            fileInfo -> name ->{ fileInfo.setFileName(name); return fileInfo;};
//    
//    private final Function<FileInfo,Function<Long,FileInfo>> provideFileSize = 
//            fileInfo ->  size ->{ fileInfo.setFileSize(size); return fileInfo;};
//    
//     private final Function<Path,Function<FileInfo,FileInfo>> providePath = 
//          Path -> fileInfo -> { fileInfo.setFilePath(Path); return fileInfo;};
//   
    
    public Path getRandomPath(){
        return Paths.get(getSpoolDir().toString(),
               Paths.get("_OCR_" + UUID.randomUUID().toString()).toString());
      
    }
    
    public Optional<Path> writeToStore(UploadedFile uploadedFile){
       //String hash = encryptionService.hash(Files.newInputStream(uploadedFile.));
       Path filePath = Paths.get(getCurrentStoreSubDirectory().toString(),
               Paths.get(UUID.randomUUID().toString()).toString());
       if(Files.notExists(filePath)){
          try(OutputStream out = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW);
               InputStream in = uploadedFile.getInputstream();) {
               IOUtils.copy(in, out);
               return Optional.ofNullable(filePath);
           } catch (IOException ex) {
               Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       return Optional.empty();
    }
    
    private Path getCurrentStoreSubDirectory(){
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = 
                DateTimeFormatter.ofPattern(Constants.STORE_SUB_DIR_NAME_DATE_FORMAT);
        Path todayPath = Paths.get(getStoreDir().toString(), 
                Paths.get(today.format(formatter)).toString());
        if(Files.notExists(todayPath)){
            try {
               return Files.createDirectories(todayPath);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return todayPath;        
    }
    
    public Path getHomeDir(){
        return Paths.get(Constants.APPLICATION_HOME_DIR);
    }
    
    public Path getSpoolDir(){
        return Paths.get(Constants.APPLICATION_SPOOL_DIR);
    }
    
    public Path getStoreDir(){
        return Paths.get(Constants.APPLICATION_STORE_DIR);
    }
    
    public Path getIndexDir(){
        return Paths.get(Constants.APPLICATION_INDEXES_DIR);
    }
    
}
