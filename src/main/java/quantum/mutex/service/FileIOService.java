/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.primefaces.model.UploadedFile;
import quantum.functional.api.Result;
import quantum.functional.api.Tuple;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.dao.InodeGroupDAO;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dto.Fragment;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.entity.InodeGroup;
import quantum.mutex.util.SupportedArchiveMimeType;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.util.Constants;
import quantum.mutex.util.EnvironmentUtils;
import quantum.mutex.util.SupportedRegularMimeType;

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
    @Inject InodeDAO inodeDAO;
    @Inject InodeGroupDAO inodeGroupDAO;
    @Inject GroupDAO groupDAO;
    
    private List<String> archiveMimeTypes;
    private List<String> regularMimeTypes;
    
    @PostConstruct
    public void init(){
        archiveMimeTypes = EnumSet.allOf(SupportedArchiveMimeType.class)
                .stream().map(e -> e.value())
                .collect(Collectors.toList());
        
        regularMimeTypes = EnumSet.allOf(SupportedRegularMimeType.class)
                .stream().map(e -> e.value())
                .collect(Collectors.toList());
    }
    
    public void createHomeDir(){
       
       if(Files.notExists(getHomeDir())){
           try {
               Files.createDirectories(getHomeDir());
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
           }
       }
      
    }
    
//    public void createSpoolDir(){
//      if(Files.notExists(getSpoolDir())){
//           try {
//               Files.createDirectories(getSpoolDir());
//           } catch (IOException ex) {
//               LOG.log(Level.SEVERE, null, ex);
//           }
//       }
//    }
    
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
        if(archiveMimeTypes.contains(uploadedFile.getContentType())){
            LOG.log(Level.INFO, "--> ARCHIVE FILE...");
            return processArchiveFile(uploadedFile,group);
        }
        
        if(regularMimeTypes.contains(uploadedFile.getContentType())){
            LOG.log(Level.INFO, "--> REGULAR FILE...");
            return processRegularFile(uploadedFile, group);
        }
        
        var message = "["+ uploadedFile.getFileName() + "]" + ": ce format de fichier n'est pas supporté. ";
        return List.of(Result.failure(message));
    }
    
    private List<Result<FileInfo>> processRegularFile(@NotNull UploadedFile uploadedFile,@NotNull Group group){
        try(InputStream inStr = uploadedFile.getInputstream();) {
            Result<Path> resPath = writeToStore(inStr,group);
            Result<FileInfo> fileInfo = buildFileInfo(resPath, uploadedFile, group);
            return List.of(fileInfo);
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.EMPTY_LIST;
        }
    }
    
     
    private List<Result<FileInfo>> processArchiveFile(@NotNull UploadedFile uploadedFile,@NotNull Group group){
        List<Tuple<Result<ArchiveEntry>,Result<Path>>> resPaths = createArchiveFilePaths(uploadedFile,group);
        return resPaths.stream().map(rp -> buildFileInfo(rp, group))
                .collect(Collectors.toList());
    }
    
    private List<Tuple<Result<ArchiveEntry>,Result<Path>>> createArchiveFilePaths(@NotNull UploadedFile uploadedFile,@NotNull Group group){
        List<Tuple<Result<ArchiveEntry>,Result<Path>>> entryPathPairs = new ArrayList<>();
        InputStream bufferedInput = null;
        InputStream compressedInput = null;
        ArchiveInputStream archiveInputStream = null;
        
        try {
            bufferedInput = new BufferedInputStream(uploadedFile.getInputstream());
            compressedInput = new CompressorStreamFactory().createCompressorInputStream(bufferedInput);
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CompressorException ex) {
            LOG.log(Level.WARNING, "Archive [{0}] is not compressed.",uploadedFile.getFileName());
        }
        
        try {
            if(compressedInput != null){
                archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(compressedInput);
            }else{
                archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(bufferedInput);
            }
            
            ArchiveEntry entry ;
            while( (entry = archiveInputStream.getNextEntry()) != null ){
                if(!archiveInputStream.canReadEntryData(entry)){
                    LOG.log(Level.INFO, "---> CANNOT READ ENTRY: {0}", entry.getName());
                    continue;
                }
                LOG.log(Level.INFO, "---> ENTRY NAME: {0}", entry.getName());
                Result<Path> resPath = writeToStore(archiveInputStream,group);
                Tuple<Result<ArchiveEntry>,Result<Path>> tuple = new Tuple(Result.of(entry),resPath);
                entryPathPairs.add(tuple);
            }
             
        } catch (ArchiveException | IOException ex) {
                Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
        closeIntputStream(archiveInputStream);
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
    
    public Result<Path> writeToStore(@NotNull InputStream inputStream,@NotNull Group group){
        Result<Path> filePath = createFilePath(getGroupStoreDirPath(group).toString(), 
                UUID.randomUUID().toString());
        
        Result<OutputStream> outStr = filePath.flatMap(p -> getOutput(p));
        Result<InputStream> inStr = Result.of(inputStream);
        
        Result<Integer> res = inStr.map(in -> outStr.flatMap(ou -> copy(in,ou)))
                .getOrElse(() -> Result.failure("Error when creating file."));
        outStr.forEach(out -> closeOutputStream(out));
        
        return res.isSuccess() ? filePath : Result.failure("Error when creating file.");
    }
    
    private Result<Path> createFilePath(@NotNull String storeDir,@NotNull String name){
       return Result.of(Paths.get(storeDir, Paths.get(name).toString()));
    }
      
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
    
    public void download(@NotNull FacesContext facesContext,@NotNull Fragment fragment){
        
        Result<Inode> rInode = inodeDAO.findById(UUID.fromString(fragment.getInodeUUID()));
        Result<Group> rGroup = rInode.flatMap(i -> inodeGroupDAO.findByInode(i).map(ig -> ig.getGroup()));
        
        Result<Path> rPath = rInode.map(Inode::getFilePath)
                .flatMap(p -> rGroup.map(g -> getInodeAbsolutePath(g, p)));
        Result<ExternalContext> rEctx = rInode.flatMap(i -> obtainExternalContext(facesContext, i));
        
        Result<InputStream> rIn = rPath.flatMap(p -> obtainInput(p));
        Result<OutputStream> rOu = rEctx.flatMap(ec -> obtainOutput(ec));
        
        rIn.forEachOrException(in -> rOu.forEach(ou -> copyAll(in, ou)))
                .forEach(ex -> LOG.log(Level.SEVERE, ExceptionUtils.getStackTrace(ex)));
        
        rIn.forEach(in -> closeIntputStream(in));
        rOu.forEach(ou -> closeOutputStream(ou));
        
        facesContext.responseComplete();
 
    }
    
    private Result<ExternalContext> obtainExternalContext(@NotNull FacesContext fc,@NotNull Inode inode){
        ExternalContext ec = fc.getExternalContext();
        ec.setResponseContentType(inode.getFileContentType());
        ec.setResponseContentLength((int)inode.getFileSize());
        var contentValue = "attachment; filename=" + inode.getFileName() ;
        ec.setResponseHeader("Content-Disposition",contentValue);
        
        return Result.success(ec);
    }
    
    private Result<InputStream> obtainInput(Path path){
        try {
            return Result.success(Files.newInputStream(path));
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }
    
    private Result<OutputStream> obtainOutput(ExternalContext ec){
        try {
            return Result.success(ec.getResponseOutputStream());
        } catch (IOException ex) {
            return Result.failure(ex);
        }
    }
    
    private void copyAll(InputStream in,OutputStream ou){
        try {
            int nRead;
            byte[] buffer = new byte[1024];
            while ((nRead = in.read(buffer)) != -1) {
                ou.write(buffer, 0, nRead);
            }
            ou.flush();
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
    private void closeOutputStream(OutputStream outputStream){
        try {
            if(outputStream != null)
                outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void closeIntputStream(InputStream inputStream){
        try {
            if(inputStream != null)
                inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    
    public Result<Path> createGroupStoreDir(@NotNull Group group){
        if(Files.notExists(getGroupStoreDirPath(group))){
           try {
               return Result.success(Files.createDirectories(getGroupStoreDirPath(group)));
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
               return Result.failure("Impossible de créer le dossier.");
           }
       }else{
            return Result.of(getGroupStoreDirPath(group));
        }
    }
    
    private String getStoreDirName(@NotNull Group group){
        return environmentUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
                + "$" + group.getName().replaceAll(" ", "_").toLowerCase();
   }
    
   private Path getFilePath(@NotNull Group group,@NotNull String fileUUID){
       return Paths.get(getGroupStoreDirPath(group).toString(), fileUUID);
   }
    
    public Path getInodeAbsolutePath(@NotNull Group group,String fileName){
        return getGroupStoreDirPath(group).resolve(fileName);
    }
   
    private Path getGroupStoreDirPath(@NotNull Group group){
        var path = Paths.get(getStoreDir().toString(), getStoreDirName(group));
        LOG.log(Level.INFO, "-->-- CURRENT FILE PATH: {0}", path.toFile());
        return  path;
   }
    
    public Path getHomeDir(){
        return Paths.get(Constants.APPLICATION_HOME_DIR);
    }
    
//    public Path getSpoolDir(){
//        return Paths.get(Constants.APPLICATION_SPOOL_DIR);
//    }
    
    public Path getStoreDir(){
        return Paths.get(Constants.APPLICATION_STORE_DIR);
    }
    
    public Path getIndexDir(){
        return Paths.get(Constants.APPLICATION_INDEXES_DIR);
    }
    
}
