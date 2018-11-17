/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.function.Function;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.functional.api.Nothing;
import quantum.functional.api.Result;
import quantum.mutex.domain.MutexFile;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dao.UserRoleDAO;
import quantum.mutex.domain.dao.MutexFileDAO;


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
    
    public Result<FileInfoDTO> handle(@NotNull FileInfoDTO fileInfoDTO){
        
        Result<quantum.mutex.domain.MutexFile> newFile = Result.of(new MutexFile());
        Result<Group> getPrimaryGroup = getCurrentUser.apply(Nothing.instance).flatMap(n -> getPrimaryGroup_.apply(n));
        
        return newFile.map(fl -> provideMetadatas.apply(fileInfoDTO).apply(fl))
            .flatMap(fi -> getTenant.apply(Nothing.instance).map(t -> provideTenant.apply(fi).apply(t)))
            .flatMap(fi -> getCurrentUser.apply(Nothing.instance).map(u -> provideOwner.apply(fi).apply(u)))
            .flatMap(fi -> getPrimaryGroup.map(g -> provideOwnerGroup.apply(fi).apply(g)))
            .flatMap(fileDAO::makePersistent)
            .map(fi -> provideFile.apply(fileInfoDTO).apply(fi));
    }
    
    private final Function<FileInfoDTO,Function<MutexFile,quantum.mutex.domain.MutexFile>> 
            provideMetadatas = fileInfo -> file -> {
                
        file.setFileName(fileInfo.getFileName());
        file.setFileSize(fileInfo.getFileSize());
        file.setFileContentType(fileInfo.getFileContentType());
        file.setFileHash(fileInfo.getFileHash());
        file.setFileLanguage(fileInfo.getFileLanguage());
        
        return file;
    };
    
    private final Function<FileInfoDTO,Function<MutexFile,FileInfoDTO>> provideFile = fileInfo -> file -> {
        
        fileInfo.setFile(file); return fileInfo;
    };
    
    private final Function<Nothing,Result<User>> getCurrentUser = n -> {
        return userDAO.findByLogin(context.getCallerPrincipal().getName());
    }; 
  
    private final Function<Nothing,Result<Tenant>> getTenant = n -> {
        return this.getCurrentUser.apply(n).map(User::getTenant);
    };
  
    private final Function<User,Result<Group>> getPrimaryGroup_ = u -> {
        return userGroupDAO.findUserPrimaryGroup(u).map(UserGroup::getGroup);
    };
  
    private final Function<MutexFile,Function<Tenant,MutexFile>> provideTenant = file -> tenant ->{
       file.setTenant(tenant); return file;
    };

    private final Function<MutexFile,Function<User,MutexFile>> provideOwner = file -> user -> {
        file.setOwnerUser(user); return file;
    };

    private final Function<MutexFile,Function<Group,MutexFile>> provideOwnerGroup = file -> group -> {
        file.setOwnerGroup(group); return file;
    };

}
