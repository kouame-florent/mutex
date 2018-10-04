/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;




import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import quantum.mutex.common.Function;
import quantum.mutex.common.Nothing;
import quantum.mutex.common.Result;
import quantum.mutex.domain.File;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.FileDAO;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dao.UserRoleDAO;


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
    @Inject FileDAO fileDAO;
    
    public Result<FileInfoDTO> handle(@NotNull FileInfoDTO fileInfoDTO){
        
        Result<quantum.mutex.domain.File> newFile = Result.of(new File());
        Result<Group> getPrimaryGroup = getCurrentUser.apply(Nothing.instance).flatMap(getPrimaryGroup_);
        
        return newFile.map(fl -> provideMetadatas.apply(fileInfoDTO).apply(fl))
                .flatMap(fi -> getTenant.apply(Nothing.instance).map(t -> provideTenant.apply(fi).apply(t)))
                .flatMap(fi -> getCurrentUser.apply(Nothing.instance).map(u -> provideOwner.apply(fi).apply(u)))
                .flatMap(fi -> getPrimaryGroup.map(g -> provideOwnerGroup.apply(fi).apply(g)))
                .flatMap(fileDAO::makePersistent)
                .map(fi -> provideFile.apply(fileInfoDTO).apply(fi));
  
    }
    
    private final Function<FileInfoDTO,Function<File,quantum.mutex.domain.File>> 
            provideMetadatas = fileInfo -> file -> {
                
        file.setFileName(fileInfo.getFileName());
        file.setFileSize(fileInfo.getFileSize());
        file.setFileContentType(fileInfo.getFileContentType());
        file.setFileHash(fileInfo.getFileHash());
        file.setFileLanguage(fileInfo.getFileLanguage());
        
        return file;
    };
    
    private final Function<FileInfoDTO,Function<File,FileInfoDTO>> provideFile = fileInfo -> file -> {
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
  
    private final Function<File,Function<Tenant,File>> provideTenant = file -> tenant ->{
       file.setTenant(tenant); return file;
    };

    private final Function<File,Function<User,File>> provideOwner = file -> user -> {
        file.setOwnerUser(user); return file;
    };

    private final Function<File,Function<Group,File>> provideOwnerGroup = file -> group -> {
        file.setOwnerGroup(group); return file;
    };

}
