/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.functional.api.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.Permission;
import quantum.mutex.domain.User;
import quantum.mutex.dto.VirtualPageDTO;
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
    
    @Inject MutextFileService mxFileSvc;
    
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
    
//    public List<VirtualPageDTO> withPermissions(List<VirtualPageDTO> virtualPages){
//        return Stream.of(withOwnerShip(virtualPages),
//                    withOwnerReadPermissions(virtualPages),
//                    withGroupReadPermissions(virtualPages),
//                    withOtherReadPermissions(virtualPages))   
//              .flatMap(List::stream).distinct()
//              .collect(Collectors.toList());
//    }
    
    private List<VirtualPageDTO> withOwnerShip(List<VirtualPageDTO> virtualPages){
        return virtualPages.stream()
                .filter(vp -> currentUser.map(u -> isFileOwner.apply(u).apply(vp)).getOrElse(() -> Boolean.FALSE) )
                .collect(Collectors.toList());
    }
    
//    private List<VirtualPageDTO> withOwnerReadPermissions(List<VirtualPageDTO> virtualPages){
//        return virtualPages.stream()
//                .filter( vp -> hasOwnerReadPermission.apply(vp))
//                .collect(Collectors.toList());
//    }
//    
//    private List<VirtualPageDTO> withGroupReadPermissions(List<VirtualPageDTO> virtualPages){
//        return virtualPages.stream()
//                .filter(vp -> currentUser.map(u -> isInFileGroup.apply(u).apply(vp)).getOrElse(() -> Boolean.FALSE) )
//                .filter(vp -> hasGroupReadPermission.apply(vp))
//                .collect(Collectors.toList());
//    }
//    
//    private List<VirtualPageDTO> withOtherReadPermissions(List<VirtualPageDTO> virtualPages){
//        return virtualPages.stream()
//                .filter(vp -> hasOtherReadPermission.apply(vp))
//                .collect(Collectors.toList());
//    }
     
   
    private final Function<User,Function<VirtualPageDTO,Boolean>> isFileOwner = u  -> vp-> {
        
        Result<User> user = mxFileSvc.get(vp).map(mx -> mx.getOwnerUser());
        return user.exists(us -> us.equals(u));

    };
    
    private final Function<User,Function<VirtualPageDTO,Boolean>> isInFileGroup = u  -> vp-> {
       Result<Group> filegroup = mxFileSvc.get(vp).map(mx -> mx.getOwnerGroup());
       List<Group> ugs = userGroupDAO.findByUser(u).stream()
               .map(ug -> ug.getGroup()).collect(Collectors.toList());
       
       return filegroup.exists(fg -> ugs.contains(fg));
    };

//    private final Function<VirtualPageDTO,Boolean> hasOwnerReadPermission = vp -> {
//        return mxFileSvc.get(vp).map(mx -> mx.getOwnerPermissions())
//                    .exists(ps -> ps.contains(Permission.READ));
//        
//    };
//              
//    private final Function<VirtualPageDTO,Boolean> hasGroupReadPermission = vp -> {
//        return mxFileSvc.get(vp).map(mx -> mx.getGroupPermissions())
//                    .exists(ps -> ps.contains(Permission.READ));
//
//    }; 
    
//     private final Function<VirtualPageDTO,Boolean> hasOtherReadPermission = vp -> {
//        return mxFileSvc.get(vp).map(mx -> mx.getOtherPermissions())
//                    .exists(ps -> ps.contains(Permission.READ));
//
//    }; 
    
}
