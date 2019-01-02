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
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.entity.Tenant;
import quantum.mutex.domain.entity.User;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dao.UserRoleDAO;
import quantum.mutex.domain.entity.InodeGroup;
import quantum.mutex.domain.service.UserGroupService;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.dao.InodeGroupDAO;


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
    @Inject InodeDAO inodeDAO;
    @Inject UserGroupService userGroupService;
    @Inject InodeGroupDAO inodeGroupDAO;
    
    public Result<FileInfo> handle(@NotNull FileInfo fileInfo){
        
//        Result<Inode> inode = Result.of(new Inode());
          Result<Inode> inode = saveInode(new Inode(),fileInfo);
          Result<InodeGroup> inodeGroup = saveInodeGroup(fileInfo);
          
          return inode.flatMap(i -> setInode(fileInfo, i));
//        Result<Group> getPrimaryGroup = getCurrentUser.apply(Nothing.instance).flatMap(n -> getPrimaryGroup_.apply(n));
        
//        inode.map(fl -> provideMetadatas.apply(fileInfo).apply(fl))
//            .flatMap(fi -> getTenant.apply(Nothing.instance).map(t -> provideTenant.apply(fi).apply(t)))
////            .flatMap(fi -> getCurrentUser.apply(Nothing.instance).map(u -> provideOwner.apply(fi).apply(u)))
////            .flatMap(fi -> getPrimaryGroup.map(g -> provideGroup.apply(fi).apply(g)))
//            .map(i -> saveInode(i, fileInfo));
            

//        return Result.empty();
    }
    
   
    
//    private Result<Inode> saveInode(Inode mutexFile,FileInfo fileInfo){
//       if(mutexFileGroupDAO.findByGroupAndHash(fileInfo.getGroup(), 
//                fileInfo.getFileHash()).isEmpty() ){
//           saveMutexFile(mutexFile);
//           saveMutexFileGroup(mutexFile, fileInfo.getGroup());
//       }
//    }
   
    private Result<Inode> saveInode(Inode newInode,FileInfo fileInfo){
        return provideMetadatas(newInode,fileInfo)
                 .flatMap(i -> saveInode_(i, fileInfo));
    }
    
    private Result<Inode> provideMetadatas(Inode inode,FileInfo fileInfo){
        inode.setFileName(fileInfo.getFileName());
        inode.setFileSize(fileInfo.getFileSize());
        inode.setFileContentType(fileInfo.getFileContentType());
        inode.setFileHash(fileInfo.getFileHash());
        inode.setFileLanguage(fileInfo.getFileLanguage());
        return Result.of(inode);
    }
 
    private Result<Inode> saveInode_(Inode newInode,FileInfo fileInfo){
       Result<Inode> inode = inodeDAO.findByHash(fileInfo.getFileHash());
       if(!inodeDAO.findByHash(fileInfo.getFileHash()).isEmpty()){
            return inodeDAO.makePersistent(newInode);
        }
        return inode;
    }
    
    private Result<InodeGroup> saveInodeGroup(FileInfo fileInfo){
        Result<InodeGroup> inodeGroup = 
                inodeGroupDAO.findByGroupAndHash(fileInfo.getGroup(), fileInfo.getFileHash());
        if(!inodeGroup.isEmpty()){
            return Result.of(new InodeGroup(fileInfo.getGroup(), fileInfo.getInode()));
        }
        return Result.failure(new Exception(getFileExistMessage(fileInfo)));
    }
    
    private String getFileExistMessage(FileInfo fileInfo){
        return "Le fichier " + "'" + fileInfo.getFileName() + "'" 
                + " existe déjà dans le groupe '" + fileInfo.getGroup().getName()
                + "'";
    }
    
    private Result<FileInfo> setInode(FileInfo fileInfo,Inode inode){
        fileInfo.setInode(inode);
        return Result.of(fileInfo);
    }
    
//    private final Function<FileInfo,Function<Inode,FileInfo>> setInode = fileInfo -> inode -> {
//        fileInfo.setInode(inode); return fileInfo;
//    };
    
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
//    private final Function<Inode,Function<Tenant,Inode>> provideTenant = inode -> tenant ->{
//       inode.setTenant(tenant); return inode;
//    };

//    private final Function<MutexFile,Function<User,MutexFile>> provideOwner = file -> user -> {
//        file.setOwnerUser(user); 
//        return file;
//    };

//    private final Function<MutexFile,Function<Group,MutexFile>> provideGroup = file -> group -> {
//        file.setGroup(group);
//        return file;
//    };

}
