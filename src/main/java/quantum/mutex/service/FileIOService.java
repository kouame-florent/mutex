/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.io.BufferedInputStream;
import java.io.File;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;
import quantum.functional.api.Result;
import quantum.functional.api.Tuple;
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
    
    public List<Result<FileInfo>> handle(UploadedFile uploadedFile,Group group){
        var types = List.of("application/x-rar-compressed","application/zip",
                 "application/x-7z-compressed","application/x-tar","application/x-zip-compressed",
                 "application/x-bzip","application/x-bzip2");
        if(types.contains(uploadedFile.getContentType())){
            return processArchiveFile(uploadedFile,group);
        }
        return processRegularFile(uploadedFile, group);
    }
    
    private List<Result<FileInfo>> processRegularFile(@NotNull UploadedFile uploadedFile,@NotNull Group group){
        try(InputStream inStr = uploadedFile.getInputstream();) {
            Result<Path> resPath = writeToSpool(inStr);
            Result<FileInfo> withGroup = buildFileInfo(resPath, uploadedFile, group);
            return List.of(withGroup);
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.EMPTY_LIST;
        }
    }
    
    private List<Result<FileInfo>> processArchiveFile(@NotNull UploadedFile uploadedFile,@NotNull Group group){
        List<Tuple<Result<ArchiveEntry>,Result<Path>>> resPaths = createArchiveFilePaths(uploadedFile);
        return resPaths.stream().map(rp -> buildFileInfo(rp, group))
                .collect(Collectors.toList());
    }
    
    private List<Tuple<Result<ArchiveEntry>,Result<Path>>> createArchiveFilePaths(@NotNull UploadedFile uploadedFile){
        List<Tuple<Result<ArchiveEntry>,Result<Path>>> entryPathPairs = new ArrayList<>();
        try(InputStream fi = uploadedFile.getInputstream();
                InputStream bi = new BufferedInputStream(fi);
//                InputStream gzi = new CompressorStreamFactory().createCompressorInputStream(bi);
                ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(bi);
//                ZipInputStream archiveInputStream = new ZipInputStream(bi);
                ){
            
            ArchiveEntry entry = null;
//              ZipEntry entry = null;
          
            while( (entry = archiveInputStream.getNextEntry()) != null ){
                if(!archiveInputStream.canReadEntryData(entry)){
                    LOG.log(Level.INFO, "---> CANNOT READ ENTRY: {0}", entry.getName());
                    continue;
                }
                LOG.log(Level.INFO, "---> ENTRY NAME: {0}", entry.getName());
                Result<Path> resPath = writeToSpool(archiveInputStream);
                Tuple<Result<ArchiveEntry>,Result<Path>> tuple = new Tuple(Result.of(entry),resPath);
                entryPathPairs.add(tuple);
            }
        
        } catch (IOException | ArchiveException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entryPathPairs;
    }
    
    private Result<FileInfo> buildFileInfo(@NotNull Result<Path> path,
            @NotNull UploadedFile uploadedFile,@NotNull Group group){
        
        Result<String> hash = path.flatMap(p -> fileInfoService.buildHash(p));
        Result<FileInfo> fileInfo = fileInfoService.newFileInfo(uploadedFile);
        Result<FileInfo> withPath = path.flatMap(p -> fileInfo.map(fi -> {fi.setFilePath(p);return fi; }) );
        Result<FileInfo> withHash = hash.flatMap(h -> withPath.map(wp -> {wp.setFileHash(h); return wp; }) );
        Result<FileInfo> withGroup = withHash.map(wh -> {wh.setGroup(group); return wh;} );
        
        return withGroup;
    }
    
    private Result<FileInfo> buildFileInfo(@NotNull Tuple<Result<ArchiveEntry>,Result<Path>> tuple,
            @NotNull Group group){
        
        Result<String> hash = tuple._2.flatMap(p -> fileInfoService.buildHash(p));
        Result<FileInfo> fileInfo = tuple._1.flatMap(arc -> fileInfoService.newFileInfo(arc));
        Result<FileInfo> withPath = tuple._2.flatMap(p -> fileInfo.map(fi -> {fi.setFilePath(p);return fi; }) );
        Result<FileInfo> withHash = hash.flatMap(h -> withPath.map(wp -> {wp.setFileHash(h); return wp; }) );
        Result<FileInfo> withGroup = withHash.map(wh -> {wh.setGroup(group); return wh;} );
        
        return withGroup;
    }
    
    public Result<Path> writeToSpool(@NotNull InputStream inputStream){

        Result<Path> path = createTempFilePath.apply(getSpoolDir().toString())
                .apply(provideUUID.get());
        
        Result<OutputStream> outStr = path.flatMap(p -> getOutput(p));
        Result<InputStream> inStr = Result.of(inputStream);
        
        Result<Integer> res = inStr.map(in -> outStr.flatMap(ou -> copy(in,ou)))
                .getOrElse(() -> Result.failure("Error when creating file."));
        outStr.forEach(out -> closeOutputStream(out));
        
        return res.isSuccess() ? path : Result.failure("Error when creating file.");
    }
     
    private final Function<String,Function<String,Result<Path>>> createTempFilePath = spoolDir 
            -> uuid -> Result.of(Paths.get(spoolDir, Paths.get(uuid).toString()));
    
    private final Supplier<String> provideUUID = () -> UUID.randomUUID().toString();
     
    private Result<OutputStream> getOutput(Path path){
        try{
            return Result.success(Files.newOutputStream(path, 
                    StandardOpenOption.CREATE_NEW));
        }catch(IOException ex){
            return Result.failure(ex);
        }
    }

    private Result<Integer> copy(InputStream in,OutputStream out){
        try {
            return  Result.success(IOUtils.copy(in, out));
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
    }
       
    private void closeOutputStream(OutputStream outputStream){
        try {
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
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
