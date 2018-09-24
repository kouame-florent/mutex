/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;



import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
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
import quantum.mutex.domain.GroupType;
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
    
//    @Inject File newFile;
    
   
    
    public FileInfoDTO handle(@NotNull FileInfoDTO fileInfoDTO){
        LOG.log(Level.INFO, "||---|||->>FILE NAME: {0}", fileInfoDTO.getFileName());
        
        Result<quantum.mutex.domain.File> newFile = Result.of(new File());
        
        newFile.map(fl -> provideMetadatas.apply(fileInfoDTO).apply(fl))
               .map(fi -> provideTenant.apply(fi));
               
        
               
               
//        File fileWithMeta = provideMetadatas(fileInfoDTO);
//        File fileWithSecurity = setSecurityDatas(fileWithMeta);
        
//        Result<File> optFile = fileDAO.makePersistent(fileWithSecurity);
//        
//        if(optFile.isPresent()){
//            fileUploadedDTO.setDocument(optFile.get());
//        }
        
      
       return fileInfoDTO;
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
    
//    private File setMetadatas(FileInfoDTO fileUploadedDTO){
//        newFile.setFileName(fileUploadedDTO.getFileName());
//        newFile.setFileSize(fileUploadedDTO.getFileSize());
//        newFile.setFileContentType(fileUploadedDTO.getFileContentType());
//        newFile.setFileHash(fileUploadedDTO.getFileHash());
//        newFile.setFileLanguage(fileUploadedDTO.getFileLanguage());
//        
//        return newFile;
//    }
    
//  private Function<File,Result<File>> provideSecurityDatas = file -> {
//      
//  };
    
  
  
    
  private final Function<Nothing,Result<User>> getCurrentUser = n -> {
      return userDAO.findByLogin(context.getCallerPrincipal().getName());
  }; 
  
  private final Function<Nothing,Result<Tenant>> getTenant = n -> {
      return this.getCurrentUser.apply(n).map(User::getTenant);
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
    
  private File setSecurityDatas(File file){

//        Optional<User> optUser = userDAO.findByLogin(context.getCallerPrincipal().getName());
        
//        if(optUser.isPresent()){
//            User user = optUser.get();
//            file.setTenant(user.getTenant());
//            file.setOwnerUser(user);
//            
//            List<UserGroup> groups = userGroupDAO.findByUserAndGroupType(user, GroupType.PRIMARY);
//            if(!groups.isEmpty()){
//                file.setOwnerGroup(groups.get(0).getGroup());
//            }
//        }
        return file;
    }
    
}
