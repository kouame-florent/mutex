/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;
import io.mutex.user.repository.GroupDAO;
import io.mutex.index.repository.InodeDAO;
import io.mutex.index.repository.InodeGroupDAO;
import io.mutex.search.valueobject.FileInfo;
import io.mutex.search.valueobject.Fragment;
import io.mutex.user.entity.Group;
import io.mutex.index.entity.Inode;
import io.mutex.shared.service.EncryptionService;
import io.mutex.index.valueobject.SupportedArchiveMimeType;
import io.mutex.index.config.GlobalConfig;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.index.valueobject.SupportedRegularMimeType;
import io.mutex.shared.event.GroupCreated;
import io.mutex.shared.event.GroupDeleted;
import io.mutex.user.service.UserGroupService;
import javax.enterprise.event.Observes;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Florent
 */
@Stateless
public class FileIOService {

    private static final Logger LOG = Logger.getLogger(FileIOService.class.getName());
     
    @Inject UserGroupService userGroupService;
    @Inject EnvironmentUtils environmentUtils;
    @Inject InodeDAO inodeDAO;
    @Inject InodeGroupDAO inodeGroupDAO;
    @Inject GroupDAO groupDAO;
    
    private List<String> archiveMimeTypes;
    private List<String> regularMimeTypes;
    
    @PostConstruct
    public void init(){
        archiveMimeTypes = EnumSet.allOf(SupportedArchiveMimeType.class)
                .stream().map(e -> e.mime())
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
    
    public List<Optional<FileInfo>> buildFilesInfo(@NotNull UploadedFile uploadedFile,@NotNull Group group){
        if(archiveMimeTypes.contains(uploadedFile.getContentType())){
            LOG.log(Level.INFO, "--> ARCHIVE FILE...");
            return processArchiveFile(uploadedFile,group);
        }
        
        if(regularMimeTypes.contains(uploadedFile.getContentType())){
            LOG.log(Level.INFO, "--> REGULAR FILE...");
            return processRegularFile(uploadedFile, group);
        }
        
        return List.of(Optional.empty());
    }
    
    private List<Optional<FileInfo>> processRegularFile(@NotNull UploadedFile uploadedFile, @NotNull Group group){
        try(InputStream inStr = uploadedFile.getInputstream();) {
            Optional<Path> rPath = writeToStore(inStr,group);
            Optional<FileInfo> fileInfo = rPath.flatMap(p -> FileIOService.this.newFileInfo(p, uploadedFile,group));

            return List.of(fileInfo);
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.emptyList();
        }
    }
    
     
    private List<Optional<FileInfo>> processArchiveFile( UploadedFile uploadedFile, Group group){
        List<SimpleEntry<Optional<ArchiveEntry>,Optional<Path>>> entries 
                = createArchiveFilePaths(uploadedFile,group);
        return entries.stream().map(ent -> newFileInfo(ent, group))
                .collect(Collectors.toList());
    }
    
    private List<SimpleEntry<Optional<ArchiveEntry>,Optional<Path>>> createArchiveFilePaths( UploadedFile uploadedFile, Group group){
        
        List<SimpleEntry<Optional<ArchiveEntry>,Optional<Path>>> entryPathPairs = new ArrayList<>();
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
                if(!archiveInputStream.canReadEntryData(entry) || entry.isDirectory()){
                    LOG.log(Level.INFO, "---> CANNOT READ ENTRY OR IS A DIRECTORY: {0}", entry.getName());
                    continue;
                }
                LOG.log(Level.INFO, "---> ENTRY NAME: {0}", entry.getName());
                Optional<Path> resPath = writeToStore(archiveInputStream,group);
                SimpleEntry<Optional<ArchiveEntry>,Optional<Path>> tuple 
                        = new SimpleEntry(Optional.of(entry),resPath);
                entryPathPairs.add(tuple);
            }
             
        } catch (ArchiveException | IOException ex) {
                Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
        closeIntputStream(archiveInputStream);
        return entryPathPairs;
    }
    
      
    private Optional<FileInfo> newFileInfo(SimpleEntry<Optional<ArchiveEntry>,Optional<Path>> entry,
             Group group){
        
        Optional<String> rHash = entry.getValue().flatMap(p -> buildHash(p));
        Optional<Path> rPath = entry.getValue();
        Optional<ArchiveEntry> rArc = entry.getKey(); 
        Optional<FileInfo> rFileInfo = rHash.flatMap(h -> rPath.flatMap(p -> rArc.map(a -> 
                new FileInfo(a.getName(), a.getSize(), p, h, group))));
        
        return rFileInfo;
    }
    
    public Optional<Path> writeToStore( InputStream inputStream, Group group){
        Optional<Path> filePath = createFilePath(getGroupStoreDirPath(group).toString(), 
                UUID.randomUUID().toString());
        
        Optional<OutputStream> outStr = filePath.flatMap(p -> getOutputStream(p));
        Optional<InputStream> inStr = Optional.of(inputStream);
        
        Optional<Integer> res = inStr.map(in -> outStr.flatMap(ou -> copy(in,ou)))
                .orElseGet(() -> Optional.empty());
        outStr.ifPresent(out -> closeOutputStream(out));
        
        return res.isEmpty() ? Optional.empty() : filePath;
    }
    
    private Optional<Path> createFilePath(@NotNull String storeDir,@NotNull String name){
        return Optional.of(Paths.get(storeDir, Paths.get(name).toString()));
    }
    
    private Optional<FileInfo> newFileInfo( Path path, UploadedFile uploadedFile, Group group){
        
        Optional<String> rHash = buildHash(path);
        Optional<FileInfo> rFileInfo = rHash.map(h -> new FileInfo(uploadedFile.getFileName(),
                uploadedFile.getSize(), path, h, group) );
     
        return rFileInfo;
    }
  
      
    private Optional<OutputStream> getOutputStream(Path path){
        try{
            return Optional.ofNullable(Files.newOutputStream(path, 
                    StandardOpenOption.CREATE_NEW));
        }catch(IOException ex){
            return Optional.empty();
        }
    }

    private Optional<Integer> copy(InputStream in,OutputStream out){
        try {
            return  Optional.ofNullable(IOUtils.copy(in, out));
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
    
    public void download( FacesContext facesContext, Fragment fragment){
        
        Optional<Inode> rInode = inodeDAO.findById(fragment.getInodeUUID());
        Optional<Group> rGroup = rInode.flatMap(i -> inodeGroupDAO.findByInode(i).map(ig -> ig.getGroup()));
        
        Optional<Path> rPath = rInode.map(Inode::getFilePath)
                .flatMap(p -> rGroup.map(g -> getInodeAbsolutePath(g, p)));
        Optional<ExternalContext> rEctx = rInode.flatMap(i -> getExternalContext(facesContext, i));
        
        Optional<InputStream> rIn = rPath.flatMap(p -> getInputStream(p));
        Optional<OutputStream> rOu = rEctx.flatMap(ec -> FileIOService.this.getOutputStream(ec));
        
        rIn.ifPresent(in -> rOu.ifPresent(ou -> copyAll(in, ou)));
        
//                
       rIn.ifPresent(in -> closeIntputStream(in));
       rOu.ifPresent(ou -> closeOutputStream(ou));
//   
        facesContext.responseComplete();
 
    }
    
    public Optional<String> buildHash(Path path){
        try{
            return Optional.ofNullable(EncryptionService.hash(Files.newInputStream(path)));
        }catch(IOException ex){
            LOG.log(Level.INFO, "{0}", ex);
            return Optional.empty();
        }
    }
    
    private Optional<ExternalContext> getExternalContext( FacesContext fc, Inode inode){
        ExternalContext ec = fc.getExternalContext();
        ec.setResponseContentType(inode.getFileContentType());
        ec.setResponseContentLength((int)inode.getFileSize());
        var contentValue = "attachment; filename=" + inode.getFileName() ;
        ec.setResponseHeader("Content-Disposition",contentValue);
        
        return Optional.ofNullable(ec);
    }
    
    private Optional<InputStream> getInputStream(Path path){
        try {
            return Optional.ofNullable(Files.newInputStream(path));
        } catch (IOException ex) {
            LOG.log(Level.INFO, "{0}", ex);
            return Optional.empty();
        }
    }
    
    private Optional<OutputStream> getOutputStream(ExternalContext ec){
        try {
            return Optional.ofNullable(ec.getResponseOutputStream());
        } catch (IOException ex) {
            LOG.log(Level.INFO, "{0}", ex);
            return Optional.empty();
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
  
    
    public Optional<Path> createGroupStoreDir(@Observes @GroupCreated Group group){
        LOG.log(Level.INFO, "-->[MUETEX] OBSERVE CREATED GROUP...");
        if(Files.notExists(getGroupStoreDirPath(group))){
           try {
               return Optional.ofNullable(Files.createDirectories(getGroupStoreDirPath(group)));
           } catch (IOException ex) {
               LOG.log(Level.SEVERE, null, ex);
               return Optional.empty();
           }
       }else{
            return Optional.of(getGroupStoreDirPath(group));
        }
    }
    
    public void deleteGroupStoreDir(@Observes @GroupDeleted @NotNull Group group){
         LOG.log(Level.INFO, "-->[MUETEX] OBSERVE DELETED GROUP...");
        try {
            Path path = getGroupStoreDirPath(group);
            FileUtils.deleteDirectory(path.toFile());
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getStoreDirName(@NotNull Group group){
       return group.getUuid();
        
//        return environmentUtils.getUserSpaceName().replaceAll(" ", "_").toLowerCase()
//                + "$" + group.getName().replaceAll(" ", "_").toLowerCase();
   }
    
   private Path getFilePath( Group group, String fileUUID){
       return Paths.get(getGroupStoreDirPath(group).toString(), fileUUID);
   }
    
    public Path getInodeAbsolutePath( Group group,String fileName){
        return getGroupStoreDirPath(group).resolve(fileName);
    }
   
    private Path getGroupStoreDirPath(@NotNull Group group){
        var path = Paths.get(getStoreDir().toString(), getStoreDirName(group));
        LOG.log(Level.INFO, "-->-- CURRENT FILE PATH: {0}", path.toFile());
        return  path;
   }
    
    public Path getHomeDir(){
        return Paths.get(GlobalConfig.APPLICATION_HOME_DIR);
    }
 
    public Path getStoreDir(){
        return Paths.get(GlobalConfig.APPLICATION_STORE_DIR);
    }
    
    public Path getIndexDir(){
        return Paths.get(GlobalConfig.APPLICATION_INDEXES_DIR);
    }
    
}
