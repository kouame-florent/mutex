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
import quantum.mutex.domain.File;
import quantum.mutex.domain.GroupType;
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
    
    @Inject File newFile;
    
    /*
    * Save document and fire DocumentSavedEvent used by VirtualPageService
    */
    public FileInfoDTO handle(FileInfoDTO fileUploadedDTO){
        LOG.log(Level.INFO, "||---|||->>FILE NAME: {0}", fileUploadedDTO.getFileName());
        File fileWithMeta = setMetadatas(fileUploadedDTO);
        File fileWithSecurity = setSecurityDatas(fileWithMeta);
        
        fileUploadedDTO.setDocument(fileDAO.makePersistent(fileWithSecurity));
      
       return fileUploadedDTO;
    }
    
    private File setMetadatas(FileInfoDTO fileUploadedDTO){
        newFile.setFileName(fileUploadedDTO.getFileName());
        newFile.setFileSize(fileUploadedDTO.getFileSize());
        newFile.setFileContentType(fileUploadedDTO.getFileContentType());
        newFile.setFileHash(fileUploadedDTO.getFileHash());
        newFile.setFileLanguage(fileUploadedDTO.getFileLanguage());
        
        return newFile;
    }
    
    private File setSecurityDatas(File file){

        Optional<User> optUser = userDAO.findByLogin(context.getCallerPrincipal().getName());
        
        if(optUser.isPresent()){
            User user = optUser.get();
            file.setTenant(user.getTenant());
            file.setOwnerUser(user);
            
            List<UserGroup> groups = userGroupDAO.findByUserAndGroupType(user, GroupType.PRIMARY);
            if(!groups.isEmpty()){
                file.setOwnerGroup(groups.get(0).getGroup());
            }
        }
        return file;
    }
    
}
