/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service;


import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.domain.Inode;
import io.mutex.domain.User;
import mutex.index.domain.valueobject.FileInfo;
import mutex.user.repository.GroupDAO;
import mutex.user.repository.UserDAO;
import mutex.user.repository.UserGroupDAO;
import mutex.user.repository.UserRoleDAO;
import io.mutex.domain.InodeGroup;
import mutex.index.repository.InodeDAO;
import mutex.index.repository.InodeGroupDAO;
import io.mutex.domain.Group;



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
    @Inject TikaMetadataService tikaMetadataService;
    @Inject mutex.util.EnvironmentUtils envUtils; 
     
    public Optional<Inode> saveInode( FileInfo fileInfo,Map<String,String> meta){

        Optional<String> rContentType = tikaMetadataService.getContentType(meta);
        Optional<String> rLanguage = tikaMetadataService.getLanguage(meta);
        Optional<User> rUser = envUtils.getUser();
        
        Optional<Inode> rInode = rContentType
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

    private String getFileExistMessage(FileInfo fileInfo){
        return "Le fichier " + "'" + fileInfo.getFileName() + "'" 
                + " existe déjà dans le groupe '" + fileInfo.getFileGroup().getName()
                + "'";
    }
    
}
