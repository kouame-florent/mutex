/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.index.valueobject.Constants;
import io.mutex.shared.event.SpaceDeleted;
import io.mutex.user.valueobject.SpaceStatus;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.user.exception.AdminExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.SpaceNameExistException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import io.mutex.user.repository.SpaceDAO;
import javax.enterprise.event.Event;


/**
 *
 * @author Florent
 */
@Stateless
public class SpaceServiceImpl implements SpaceService{

    private static final Logger LOG = Logger.getLogger(SpaceServiceImpl.class.getName());
          
    @Inject SpaceDAO spaceDAO;
    @Inject AdminService adminService;
    
    @Inject @SpaceDeleted
    private Event<Space> spaceDeletedEvent;
        
    @Override
    public List<Space> getAllSpaces(){
       return spaceDAO.findAll().stream()
               .filter(t -> !t.getName().equalsIgnoreCase(Constants.ADMIN_DEFAULT_SPACE))
               .collect(toList());
    }
    
    @Override
    public Optional<Space> getSpaceByName(@NotBlank String name){
        return spaceDAO.findByName(name.toUpperCase(Locale.getDefault()));
    }
      
    @Override
    public Optional<Space> getSpaceByUuid(@NotBlank String uuid){
        return spaceDAO.findById(uuid);
    }
       
    @Override
    public Optional<Space> create(@NotNull Space space) throws SpaceNameExistException{
       var name = upperCaseWithoutAccent(space.getName());
       if(!isSpaceWithNameExist(name)){
            return spaceDAO.makePersistent(nameToUpperCase(space));
        }
        throw new SpaceNameExistException("Ce nom d'espace existe déjà");
    }
      
    @Override
    public Optional<Space> update(@NotNull Space space) throws SpaceNameExistException {
        var name = upperCaseWithoutAccent(space.getName());
        Optional<Space> oSpaceByName = spaceDAO.findByName(name);
       
        if((oSpaceByName.isPresent() && oSpaceByName.filter(t1 -> t1.equals(space)).isEmpty()) ){
            throw new SpaceNameExistException("Ce nom d'espace existe déjà");
        }
        return spaceDAO.makePersistent(nameToUpperCase(space));
    }
    
    private boolean isSpaceWithNameExist(@NotBlank String name){
        Optional<Space> oSpace = spaceDAO.findByName(name);
        return oSpace.isPresent();
    }
       
    private Space nameToUpperCase(@NotNull Space space){
        String newName = upperCaseWithoutAccent(space.getName());
        LOG.log(Level.INFO, "[MUTEX] SPACE NAME: {0}", newName);
        space.setName(newName);
        return space;
    }
    
    private String upperCaseWithoutAccent(@NotBlank String name){
       String[] parts = removeAccent(name).map(StringUtils::split)
               .orElseGet(() -> new String[]{});
      return Arrays.stream(parts).map(StringUtils::strip).map(String::toUpperCase)
               .collect(Collectors.joining(" "));
    }
   
    private Optional<String> removeAccent(@NotBlank String name){
       return Optional.ofNullable(StringUtils.stripAccents(name));
    }
    
    @Override
    public void delete(@NotNull Space space){
//        unlinkAdminAndChangeStatus(space);
        spaceDeletedEvent.fire(space);
        spaceDAO.makeTransient(space);  
        
    }
    
//    @Override
//    public void unlinkAdminAndChangeStatus(@NotNull Space space){
//        adminService.findBySpace(space)
////                .flatMap(adminService::unlinkAdmin)
//                .ifPresent(adm -> adminService.changeAdminStatus(adm, UserStatus.DISABLED));
//    }
//  
//    @Override
//    public void updateSpaceAdmin(@NotNull Space space, @NotNull Admin admin) 
//            throws AdminExistException, 
//            NotMatchingPasswordAndConfirmation{
//        spaceDAO.findById(space.getUuid())
//                .ifPresent(this::unlinkAdminAndChangeStatus);
//        updateSpaceAdmin_(space, admin);
//     }
    
    private Optional<Admin> updateSpaceAdmin_(@NotNull Space space, @NotNull Admin admin) 
            throws AdminExistException,
            NotMatchingPasswordAndConfirmation{
//        admin.setSpace(space);
        return adminService.createAdmin(admin);
    }
    
    @Override
    public Space changeStatus(@NotNull Space space,@NotNull SpaceStatus status){
        space.setStatus(status);
        return space;
    }

}
