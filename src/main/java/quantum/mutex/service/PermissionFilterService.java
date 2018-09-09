/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.File;
import quantum.mutex.domain.GroupType;
import quantum.mutex.domain.Permission;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.domain.VirtualPage;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;

/**
 *
 * @author Florent
 */
@Stateless
public class PermissionFilterService {

    private static final Logger LOG = Logger.getLogger(PermissionFilterService.class.getName());
    
    @Resource
    SessionContext sessionContext;
    
    @Inject UserDAO userDAO;
    @Inject UserGroupDAO userGroupDAO;
    
    Optional<User> optCurrentUser;
   
    @PostConstruct
    public void init(){
        
    }
    
    public List<VirtualPage> withPermissions(List<VirtualPage> virtualPages){
        List<VirtualPage> results = new ArrayList<>();
        optCurrentUser = userDAO.findByLogin(sessionContext.getCallerPrincipal().getName());
        if(optCurrentUser.isPresent()){
            
            results.addAll(withOwnerPermissions(virtualPages));
            results.addAll(withGroupPermissions(virtualPages));
           // results.addAll(withOtherPermissions(virtualPages));   
        }
        return results;
    }
    
    private List<VirtualPage> withOwnerPermissions(List<VirtualPage> virtualPages){
        
        LOG.log(Level.INFO, "-->> FILE OWNER: {0}", optCurrentUser.get());
       
        return virtualPages.stream()
                .peek(vp -> LOG.log(Level.INFO, "-->> BEFORE FILTER: {0}", vp.getFile().getFileName()) )
                .filter(vp -> ( vp.getFile().getOwnerUser().equals(optCurrentUser.get())
                         && (hasOwnerReadPermission(vp.getFile()))))
                .peek(vp -> LOG.log(Level.INFO, "-->> AFTER FILTER: {0}", vp.getFile().getFileName()) )
                .collect(Collectors.toList());
    }
    
    private List<VirtualPage> withGroupPermissions(List<VirtualPage> virtualPages){
//        List<UserGroup> primaryUserGroup 
//                    = userGroupDAO.findByUserAndGroupType(optCurrentUser.get(), GroupType.PRIMARY);
//        if(!primaryUserGroup.isEmpty()) {
//            return virtualPages.stream()
//                     .filter(vp -> (vp.getFile().getOwnerGroup().equals( primaryUserGroup.get(0).getGroup()) ) 
//                             && (hasGroupReadPermission(vp.getFile())) )
//                     .collect(Collectors.toList());
//        }  
//        
        return new ArrayList<>();
        
      
    }
     
    private List<VirtualPage> withOtherPermissions(List<VirtualPage> virtualPages){
            
//            List<UserGroup> primaryUserGroup 
//                    = userGroupDAO.findByUserAndGroupType(optCurrentUser.get(), GroupType.PRIMARY);
//            if(!primaryUserGroup.isEmpty()){
//                return virtualPages.stream()
//                     .filter(vp -> !(vp.getFile().getOwnerUser().equals(optCurrentUser.get())) 
//                             && !(vp.getFile().getOwnerGroup().equals(primaryUserGroup.get(0).getGroup()) ) 
//                             && (hasOtherReadPermission(vp.getFile())) )
//                     .collect(Collectors.toList());
//            }
            return new ArrayList<>();
    }
    
    private boolean hasOwnerReadPermission(File file){
        return file.getOwnerPermissions().contains(Permission.READ);
    }
    
    private boolean hasGroupReadPermission(File file){
        return file.getGroupPermissions().contains(Permission.READ);
    }
    
    private boolean hasOtherReadPermission(File file){
        return file.getOtherPermissions().contains(Permission.READ);
    }
}
