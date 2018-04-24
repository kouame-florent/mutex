/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.File;
import quantum.mutex.domain.Group;
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
    
    @Resource
    SessionContext sessionContext;
    
    @Inject UserDAO userDAO;
    @Inject UserGroupDAO userGroupDAO;
    
    Optional<User> optCurrentUser;
    
    private final List<VirtualPage> results = new ArrayList<>();
    
    @PostConstruct
    public void init(){
        optCurrentUser = userDAO.findByLogin(sessionContext.getCallerPrincipal().getName());
    }
    
    public List<VirtualPage> withPermissions(List<VirtualPage> virtualPages){
        
        if(optCurrentUser.isPresent()){
            results.addAll(withOwnerPermissions(virtualPages));
            results.addAll(withGroupPermissions(virtualPages));
            results.addAll(withOtherPermissions(virtualPages));
        }
        return results;
    }
    
    private List<VirtualPage> withOwnerPermissions(List<VirtualPage> virtualPages){
       
        return virtualPages.stream()
                 .filter(vp -> (vp.getFile().getOwnerUser() == optCurrentUser.get()) 
                         && (hasOwnerReadPermission(vp.getFile())) )
                 .collect(Collectors.toList());
    }
    
    private List<VirtualPage> withGroupPermissions(List<VirtualPage> virtualPages){
        Optional<UserGroup> primaryUserGroup 
                    = userGroupDAO.findByUserAndGroupType(optCurrentUser.get(), GroupType.PRIMARY);
            
        return virtualPages.stream()
                     .filter(vp -> (vp.getFile().getOwnerGroup() == primaryUserGroup.get().getGroup() ) 
                             && (hasGroupReadPermission(vp.getFile())) )
                     .collect(Collectors.toList());
      
    }
     
    private List<VirtualPage> withOtherPermissions(List<VirtualPage> virtualPages){
            
            Optional<UserGroup> primaryUserGroup 
                    = userGroupDAO.findByUserAndGroupType(optCurrentUser.get(), GroupType.PRIMARY);
            
            return virtualPages.stream()
                     .filter(vp -> !(vp.getFile().getOwnerUser() == optCurrentUser.get()) 
                             && !(vp.getFile().getOwnerGroup() == primaryUserGroup.get().getGroup() ) 
                             && (hasOtherReadPermission(vp.getFile())) )
                     .collect(Collectors.toList());
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
