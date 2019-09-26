/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


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
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.dao.InodeGroupDAO;
import quantum.mutex.domain.type.FileInfo;
import quantum.mutex.domain.type.Fragment;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Inode;
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
    
    public List<Optional<FileInfo>> buildFilesInfo(UploadedFile uploadedFile,Group group){
        if(archiveMimeTypes.contains(uploadedFile.getContentType())){
            LOG.log(Level.INFO, "--> ARCHIVE FILE...");
            return processArchiveFile(uploadedFile,group);
        }
        
        if(regularMimeTypes.contains(uploadedFile.getContentType())){
            LOG.log(Level.INFO, "--> REGULAR FILE...");
            return processRegularFile(uploadedFile, group);
        }
        
        var message = "["+ uploadedFile.getFileName() + "]" + ": ce format de fichier n'est pas supporté. ";
        return List.of(Optional.empty());
    }
    
    private List<Optional<FileInfo>> processRegularFile( UploadedFile uploadedFile, Group group){
        try(InputStream inStr = uploadedFile.getInputstream();) {
            Optional<Path> rPath = writeToStore(inStr,group);
            Optional<FileInfo> fileInfo = rPath.flatMap(p -> buildFileInfo(p, uploadedFile,group));
//            Optional<FileInfo> fileInfo = buildFileInfo(resPath, uploadedFile, group);
            return List.of(fileInfo);
        } catch (IOException ex) {
            Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.EMPTY_LIST;
        }
    }
    
     
    private List<Optional<FileInfo>> processArchiveFile( UploadedFile uploadedFile, Group group){
        List<SimpleEntry<Optional<ArchiveEntry>,Optional<Path>>> resPaths = createArchiveFilePaths(uploadedFile,group);
        return resPaths.stream().map(rp -> buildFileInfo(rp, group))
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
                if(!archiveInputStream.canReadEntryData(entry)){
                    LOG.log(Level.INFO, "---> CANNOT READ ENTRY: {0}", entry.getName());
                    continue;
                }
                LOG.log(Level.INFO, "---> ENTRY NAME: {0}", entry.getName());
                Optional<Path> resPath = writeToStore(archiveInputStream,group);
                SimpleEntry<Optional<ArchiveEntry>,Optional<Path>> tuple = new SimpleEntry(Optional.of(entry),resPath);
                entryPathPairs.add(tuple);
            }
             
        } catch (ArchiveException | IOException ex) {
                Logger.getLogger(FileIOService.class.getName()).log(Level.SEVERE, null, ex);
        }
        closeIntputStream(archiveInputStream);
        return entryPathPairs;
    }
    
    private Optional<FileInfo> buildFileInfo( Path path,
             UploadedFile uploadedFile, Group group){
        
        Optional<String> rHash = buildHash(path);
        Optional<FileInfo> rFileInfo = rHash.map(h -> new FileInfo(uploadedFile.getFileName(),
                uploadedFile.getSize(), path, h, group) );
     
        return rFileInfo;
    }
    
    private Optional<FileInfo> buildFileInfo(SimpleEntry<Optional<ArchiveEntry>,Optional<Path>> tuple,
             Group group){
        
        Optional<String> rHash = tuple.getValue().flatMap(p -> buildHash(p));
        Optional<Path> rPath = tuple.getValue();
        Optional<ArchiveEntry> rArc = tuple.getKey(); 
        Optional<FileInfo> rFileInfo = rHash.flatMap(h -> rPath.flatMap(p -> rArc.map(a -> 
                new FileInfo(a.getName(), a.getSize(), p, h, group))));
        
        return rFileInfo;
    }
    
    public Optional<Path> writeToStore( InputStream inputStream, Group group){
        Optional<Path> filePath = createFilePath(getGroupStoreDirPath(group).toString(), 
                UUID.randomUUID().toString());
        
        Optional<OutputStream> outStr = filePath.flatMap(p -> getOutput(p));
        Optional<InputStream> inStr = Optional.of(inputStream);
        
        Optional<Integer> res = inStr.map(in -> outStr.flatMap(ou -> copy(in,ou)))
                .orElseGet(() -> Optional.empty());
        outStr.ifPresent(out -> closeOutputStream(out));
        
        return res.isEmpty() ? Optional.empty() : filePath;
        
//        return res.isSuccess() ? filePath : Optional.failure("Error when creating file.");
    }
    
    private Optional<Path> createFilePath( String storeDir, String name){
       return Optional.of(Paths.get(storeDir, Paths.get(name).toString()));
    }
      
    private Optional<OutputStream> getOutput(Path path){
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
        Optional<ExternalContext> rEctx = rInode.flatMap(i -> obtainExternalContext(facesContext, i));
        
        Optional<InputStream> rIn = rPath.flatMap(p -> obtainInput(p));
        Optional<OutputStream> rOu = rEctx.flatMap(ec -> obtainOutput(ec));
        
        rIn.ifPresent(in -> rOu.ifPresent(ou -> copyAll(in, ou)));
        
//        rIn.forEachOrException(in -> rOu.forEach(ou -> copyAll(in, ou)))
//                .forEach(ex -> LOG.log(Level.SEVERE, ExceptionUtils.getStackTrace(ex)));
//        
        
       rIn.ifPresent(in -> closeIntputStream(in));
       rOu.ifPresent(ou -> closeOutputStream(ou));
//
//       rIn.forEach(in -> closeIntputStream(in));
//        rOu.forEach(ou -> closeOutputStream(ou));
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
    
    private Optional<ExternalContext> obtainExternalContext( FacesContext fc, Inode inode){
        ExternalContext ec = fc.getExternalContext();
        ec.setResponseContentType(inode.getFileContentType());
        ec.setResponseContentLength((int)inode.getFileSize());
        var contentValue = "attachment; filename=" + inode.getFileName() ;
        ec.setResponseHeader("Content-Disposition",contentValue);
        
        return Optional.ofNullable(ec);
    }
    
    private Optional<InputStream> obtainInput(Path path){
        try {
            return Optional.ofNullable(Files.newInputStream(path));
        } catch (IOException ex) {
            LOG.log(Level.INFO, "{0}", ex);
            return Optional.empty();
        }
    }
    
    private Optional<OutputStream> obtainOutput(ExternalContext ec){
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
  
    
    public Optional<Path> createGroupStoreDir( Group group){
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
    
    private String getStoreDirName( Group group){
        return environmentUtils.getUserTenantName().replaceAll(" ", "_").toLowerCase()
                + "$" + group.getName().replaceAll(" ", "_").toLowerCase();
   }
    
   private Path getFilePath( Group group, String fileUUID){
       return Paths.get(getGroupStoreDirPath(group).toString(), fileUUID);
   }
    
    public Path getInodeAbsolutePath( Group group,String fileName){
        return getGroupStoreDirPath(group).resolve(fileName);
    }
   
    private Path getGroupStoreDirPath( Group group){
        var path = Paths.get(getStoreDir().toString(), getStoreDirName(group));
        LOG.log(Level.INFO, "-->-- CURRENT FILE PATH: {0}", path.toFile());
        return  path;
   }
    
    public Path getHomeDir(){
        return Paths.get(Constants.APPLICATION_HOME_DIR);
    }
 
    public Path getStoreDir(){
        return Paths.get(Constants.APPLICATION_STORE_DIR);
    }
    
    public Path getIndexDir(){
        return Paths.get(Constants.APPLICATION_INDEXES_DIR);
    }
    
}
