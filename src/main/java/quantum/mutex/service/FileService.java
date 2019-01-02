/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Nothing;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.MutexFile;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.entity.UserGroup;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dao.UserRoleDAO;
import quantum.mutex.domain.dao.MutexFileDAO;
import quantum.mutex.domain.dao.MutexFileGroupDAO;
import quantum.mutex.domain.entity.MutexFileGroup;
import quantum.mutex.domain.service.UserGroupService;


/**
 *
 * @author Florent
 */
@Stateless
public class FileService {

    private static final Logger LOG = Logger.getLogger(FileService.class.getName());
    
    @Resource
    SessionContext context;
    
    @Inject UserDAO userDAO;
    @Inject UserRoleDAO userRoleDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject MutexFileDAO fileDAO;
    @Inject UserGroupService userGroupService;
    @Inject MutexFileGroupDAO mutexFileGroupDAO;
    
    public Result<FileInfo> handle(@NotNull FileInfo fileInfo){
        
        Result<quantum.mutex.domain.entity.MutexFile> newFile = Result.of(new MutexFile());
//        Result<Group> getPrimaryGroup = getCurrentUser.apply(Nothing.instance).flatMap(n -> getPrimaryGroup_.apply(n));
        
        return newFile.map(fl -> provideMetadatas.apply(fileInfo).apply(fl))
            .flatMap(fi -> getTenant.apply(Nothing.instance).map(t -> provideTenant.apply(fi).apply(t)))
//            .flatMap(fi -> getCurrentUser.apply(Nothing.instance).map(u -> provideOwner.apply(fi).apply(u)))
//            .flatMap(fi -> getPrimaryGroup.map(g -> provideGroup.apply(fi).apply(g)))
            .map(this::save)
            .map(fi -> provideFile.apply(fileInfo).apply(fi));

//        return Result.empty();
    }
    
    private final Function<FileInfo,Function<MutexFile,quantum.mutex.domain.entity.MutexFile>> 
            provideMetadatas = fileInfo -> file -> {
                
        file.setFileName(fileInfo.getFileName());
        file.setFileSize(fileInfo.getFileSize());
        file.setFileContentType(fileInfo.getFileContentType());
        file.setFileHash(fileInfo.getFileHash());
        file.setFileLanguage(fileInfo.getFileLanguage());
        
        return file;
    };
    
    private Result<MutexFile> save(MutexFile mutexFile,FileInfo fileInfo){
       if(mutexFileGroupDAO.findByGroupAndHash(fileInfo.getGroup(), 
                fileInfo.getFileHash()).isEmpty() ){
           saveMutexFile(mutexFile);
           saveMutexFileGroup(mutexFile, fileInfo.getGroup());
       }
    }
   
    private Result<MutexFile> saveMutexFile(MutexFile mutexFile){
        return fileDAO.makePersistent(mutexFile);
    }
    
    private Result<MutexFileGroup> saveMutexFileGroup(MutexFile mutexFile,Group group){
        return Result.of(new MutexFileGroup(group, mutexFile));
    }
    
    private String getFileExistMessage(MutexFile file){
        return "Le fichier " + "'" + file.getFileName() + "'" 
                + " existe déjà dans le groupe '" + file.getOwnerGroup().getName() 
                + "'";
    }
    
    private final Function<FileInfo,Function<MutexFile,FileInfo>> provideFile = fileInfo -> file -> {
        fileInfo.setFile(file); return fileInfo;
    };
    
    private final Function<Nothing,Result<User>> getCurrentUser = n -> {
        return userDAO.findByLogin(context.getCallerPrincipal().getName());
    }; 
  
    private final Function<Nothing,Result<Tenant>> getTenant = n -> {
        return this.getCurrentUser.apply(n).map(User::getTenant);
    };
  
//    private final Function<User,Result<Group>> getPrimaryGroup_ = u -> {
//        return userGroupDAO.findUserPrimaryGroup(u).map(UserGroup::getGroup);
//    };
//  
    private final Function<MutexFile,Function<Tenant,MutexFile>> provideTenant = file -> tenant ->{
       file.setTenant(tenant); return file;
    };

//    private final Function<MutexFile,Function<User,MutexFile>> provideOwner = file -> user -> {
//        file.setOwnerUser(user); 
//        return file;
//    };

//    private final Function<MutexFile,Function<Group,MutexFile>> provideGroup = file -> group -> {
//        file.setGroup(group);
//        return file;
//    };

}
