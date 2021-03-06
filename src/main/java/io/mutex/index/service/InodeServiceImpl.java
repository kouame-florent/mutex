/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;


import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.index.entity.Inode;
import io.mutex.user.entity.User;
import io.mutex.search.valueobject.FileInfo;
import io.mutex.index.repository.InodeDAO;
import io.mutex.user.entity.Searcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;



/**
 *
 * @author Florent
 */
@Stateless
public class InodeServiceImpl implements InodeService {

    private static final Logger LOG = Logger.getLogger(InodeServiceImpl.class.getName());
    

    @Inject InodeDAO inodeDAO;
    @Inject TikaMetadataService tikaMetadataService;
    @Inject io.mutex.shared.service.EnvironmentUtils envUtils; 
    
//    @Override
//    public void create(FileInfo fileInfo,Map<String,String> meta){
//    
//    }
     
    @Override
    public Optional<Inode> create( FileInfo fileInfo,Map<String,String> meta){

        Optional<String> rContentType = tikaMetadataService.getContentType(meta);
        Optional<String> rLanguage = tikaMetadataService.getLanguage(meta);
        Optional<User> rUser = envUtils.getUser();
        
        //get name without directory when file came from archive
        String fileName = Paths.get(fileInfo.getFileName()).getFileName().toString(); 
        
        Optional<Inode> rInode = rContentType
                .flatMap(c -> rLanguage
                        .flatMap(l -> rUser
                                .map(u -> new Inode(fileInfo.getFileHash(),
                                    c, fileName, fileInfo.getFileSize(),
                                    fileInfo.getFilePath().toString(), l, u,fileInfo.getFileGroup()))));
           return rInode.flatMap(i -> inodeDAO.makePersistent(i));
    }
    
//    public void saveInodeGroup(Group group, Inode inode){
//        InodeGroup inodeGroup = new InodeGroup(group, inode);
//        inodeGroupDAO.makePersistent(inodeGroup);
//    }

    private String getFileExistMessage(FileInfo fileInfo){
        return "Le fichier " + "'" + fileInfo.getFileName() + "'" 
                + " existe déjà dans le groupe '" + fileInfo.getFileGroup().getName()
                + "'";
    }
    
    @Override
    public List<Inode> getByOwner(Searcher user){
       LOG.log(Level.INFO, "--> FIND BY OWNER ...");
       List<Inode> inodes = inodeDAO.findByOwner(user);
       LOG.log(Level.INFO, "--> INODE SIZE: {0}", inodes.size());
       return inodes;
    }

    @Override
    public Optional<Inode> getByUUID(String inodeUUID) {
       return inodeDAO.findById(inodeUUID);
    }
    
}
