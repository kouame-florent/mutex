/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
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
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.dao.InodeGroupDAO;
import quantum.mutex.domain.entity.Group;


/**
 *
 * @author Florent
 */
@Stateless
public class InodeService {

    private static final Logger LOG = Logger.getLogger(InodeService.class.getName());
    
    @Resource
    SessionContext context;
    
    @Inject UserDAO userDAO;
    @Inject UserRoleDAO userRoleDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject InodeDAO inodeDAO;
    @Inject UserGroupService userGroupService;
    @Inject InodeGroupDAO inodeGroupDAO;
    @Inject InodeMetadataService inodeMetadataService;
    @Inject TikaMetadataService tikaMetadataService;
    @Inject quantum.mutex.util.EnvironmentUtils envUtils;
    
//    public void saveInodeAndInodeGroup(@NotNull FileInfo fileInfo){
//        Result<Inode> inodeWithMeta = provideMetadatas(new Inode(),fileInfo);
//        Result<Inode> persistentInode = inodeWithMeta
//                .flatMap(i -> inodeDAO.makePersistent(i));
//        persistentInode.flatMap(i -> saveInodeGroup(fileInfo.getGroup(), i));
////        inode.map(i -> saveInodeGroup(fileInfo, i));
//        return inode.flatMap(i -> setInode(fileInfo, i));
//
//    }
    
    public Result<Inode> saveInode(@NotNull FileInfo fileInfo,Map<String,String> meta){

        Result<String> rContentType = tikaMetadataService.getContentType(meta);
        Result<String> rLanguage = tikaMetadataService.getLanguage(meta);
        Result<User> rUser = envUtils.getUser();
        
        Result<Inode> rInode = rContentType
                .flatMap(c -> rLanguage
                        .flatMap(l -> rUser
                                .map(u -> new Inode(fileInfo.getFileHash(),
                                    c, fileInfo.getFileName(), fileInfo.getFileSize(),
                                    fileInfo.getFilePath().toString(), l, u))));
           return rInode.flatMap(i -> inodeDAO.makePersistent(i));
    }
    
    public void saveInodeGroup(Group group, Inode inode){
        InodeGroup inodeGroup = new InodeGroup(group, inode);
        inodeGroupDAO.makePersistent(inodeGroup);
    }
   
//    private Result<Inode> saveInode(Inode newInode,FileInfo fileInfo){
//        return provideMetadatas(newInode,fileInfo)
//                 .flatMap(i -> saveInode_(i));
//    }
    
//    private Result<Inode> provideMetadatas(Inode inode,FileInfo fileInfo){
//        inode.setFileName(fileInfo.getFileName());
//        inode.setFileSize(fileInfo.getFileSize());
//        inode.setFileContentType(fileInfo.getFileContentType());
//        inode.setFileHash(fileInfo.getFileHash());
//       // inode.setFileLanguage(fileInfo.getFileLanguage());
//        inode.setFileLanguage(inodeMetadataService.addLanguage(meta, tikaMetadata));
//        inode.setFilePath(fileInfo.getFilePath().getFileName().toString());
//        envUtils.getUser().map(u -> {inode.setOwnerUser(u); return inode;});
//      
//        LOG.log(Level.INFO, "--> CURRENT INODE: {0} ", inode);
//        return Result.of(inode);
//    }
 
//    private Result<Inode> saveInode_(Inode newInode){
//       return inodeDAO.makePersistent(newInode);
//
//    }
    
//    private Result<InodeGroup> saveInodeGroup(FileInfo fileInfo, Inode inode){
//        InodeGroup inodeGroup = new InodeGroup(fileInfo.getGroup(), inode);
//        return inodeGroupDAO.makePersistent(inodeGroup);
//   }
    
    
    
    private String getFileExistMessage(FileInfo fileInfo){
        return "Le fichier " + "'" + fileInfo.getFileName() + "'" 
                + " existe déjà dans le groupe '" + fileInfo.getFileGroup().getName()
                + "'";
    }
    
   
    
//    private final Function<Nothing,Result<User>> getCurrentUser = n -> {
//        return userDAO.findByLogin(context.getCallerPrincipal().getName());
//    }; 
//  
//    private final Function<Nothing,Result<Tenant>> getTenant = n -> {
//        return this.getCurrentUser.apply(n).map(User::getTenant);
//    };
// 

}
