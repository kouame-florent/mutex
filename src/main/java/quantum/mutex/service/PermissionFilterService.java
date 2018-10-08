/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.common.Result;
import quantum.mutex.domain.MutexFile;
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
    
    Result<User> currentUser;
   
    @PostConstruct
    public void init(){
        currentUser = initCurrentUser();
    }
    
    private Result<User> initCurrentUser(){
       return Result.of(sessionContext)
                .map(s -> s.getCallerPrincipal())
                .map(p -> p.getName())
                .flatMap(userDAO::findByLogin);
    }
    
    public List<VirtualPage> withPermissions(List<VirtualPage> virtualPages){
        List<VirtualPage> results = new ArrayList<>();
//        optCurrentUser = userDAO.findByLogin(sessionContext.getCallerPrincipal().getName());
//        if(optCurrentUser.isPresent()){
//            
//            results.addAll(withOwnerPermissions(virtualPages));
//            results.addAll(withGroupPermissions(virtualPages));
//            results.addAll(withOtherPermissions(virtualPages));   
//        }
        
        Stream.of(withOwnerShip(virtualPages).stream(),
                    withOwnerReadPermissions(virtualPages).stream(),
                    withGroupReadPermissions(virtualPages).stream(),
                    withOtherReadPermissions(virtualPages).stream())
               
                
        return results;
    }
    
    private List<VirtualPage> withOwnerShip(List<VirtualPage> virtualPages){
        return virtualPages.stream()
                .filter(vp -> currentUser.map(u -> isFileOwner.apply(u).apply(vp)).getOrElse(() -> Boolean.FALSE) )
                .collect(Collectors.toList());
    }
    
    private List<VirtualPage> withOwnerReadPermissions(List<VirtualPage> virtualPages){
        return virtualPages.stream()
                .filter( vp -> hasOwnerReadPermission.apply(vp))
                .collect(Collectors.toList());
    }
    
    private List<VirtualPage> withGroupReadPermissions(List<VirtualPage> virtualPages){
        return virtualPages.stream()
                .filter(vp -> currentUser.map(u -> isInFileGroup.apply(u).apply(vp)).getOrElse(() -> Boolean.FALSE) )
                .filter(vp -> hasGroupReadPermission.apply(vp))
                .collect(Collectors.toList());
    }
    
    private List<VirtualPage> withOtherReadPermissions(List<VirtualPage> virtualPages){
        return virtualPages.stream()
                .filter(vp -> hasOtherReadPermission.apply(vp))
                .collect(Collectors.toList());
    }
     
   
    private final Function<User,Function<VirtualPage,Boolean>> isFileOwner = u  -> vp-> {
        return vp.getMutexFile().getOwnerUser().equals(u);
    };
    
    private final Function<User,Function<VirtualPage,Boolean>> isInFileGroup = u  -> vp-> {
       return !userGroupDAO.findByUser(u).stream()
                    .filter(ug -> ug.getGroup().equals(vp.getMutexFile().getOwnerGroup())) 
                    .collect(Collectors.toList()).isEmpty();
    };

    private final Function<VirtualPage,Boolean> hasOwnerReadPermission = vp -> {
        return vp.getMutexFile().getOwnerPermissions().contains(Permission.READ);
    };
              
    private final Function<VirtualPage,Boolean> hasGroupReadPermission = vp -> {
        return vp.getMutexFile().getGroupPermissions().contains(Permission.READ);
    }; 
    
     private final Function<VirtualPage,Boolean> hasOtherReadPermission = vp -> {
        return vp.getMutexFile().getOtherPermissions().contains(Permission.READ);
    }; 
    
}
