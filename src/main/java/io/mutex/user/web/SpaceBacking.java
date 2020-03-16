/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import io.mutex.user.entity.Admin;
import io.mutex.user.entity.Space;
import io.mutex.user.exception.AdminLoginExistException;
import io.mutex.user.exception.AdminExistException;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.valueobject.SpaceStatus;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.valueobject.ViewID;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.exception.SpaceNameExistException;
import java.util.Optional;
import io.mutex.user.service.AdminService;
import io.mutex.user.service.SpaceService;


/**
 *
 * @author Florent
 */
@Named(value = "spaceBacking")
@ViewScoped
public class SpaceBacking extends QuantumMainBacking<Space> implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(SpaceBacking.class.getName());

    @Inject SpaceService spaceService;
    @Inject AdminService adminService;
  
    private Admin selectedAdmin;
    private final Set<Admin> selectedAdmins = new HashSet<>();

    @Override
    @PostConstruct
    protected void postConstruct() {
       initCtxParamKey(ContextIdParamKey.SPACE_UUID);
       initSpaces();
    }
   
    private void initSpaces() {
       initContextEntities(spaceService::getAllSpaces);
    }

    @Override
    protected String editViewId() {
        return ViewID.EDIT_SPACE_DIALOG.id();
    }
    
//    @Override
    public void delete() {
        spaceService.delete(selectedEntity);
    }
   
    private void updateAndRefresh(Space space){
        try {
           spaceService.update(space);
           initSpaces();
        } catch (SpaceNameExistException ex) {
           addGlobalErrorMessage(ex.getMessage());
        }
    }
    
    public void openAddAdmintDialog(){
        Map<String,Object> options = getDialogOptions(65, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-administrator-dlg", options, null);
    }
   
//    public void openAddAdminDialog(Space space){
//        Map<String,Object> options = getDialogOptions(65, 70,true);
//        PrimeFaces.current().dialog()
//                .openDynamic(ViewID.ADD_ADMIN_DIALOG.id(), options, 
//                        getDialogParams(ContextIdParamKey.SPACE_UUID,
//                                space.getUuid()));
//        LOG.log(Level.INFO, "-- SPACE UUID:{0}", space.getUuid());
//    }  
    
    public void unlinkAdmin(Space space){
//        spaceService.unlinkAdminAndChangeStatus(space);
    }  
    
    public void disableSpace( Space space){
        spaceService.changeStatus(space, SpaceStatus.DISABLED);
        updateAndRefresh(space);
    }
    
    public void enableSpace( Space space){
        spaceService.changeStatus(space, SpaceStatus.ENABLED);
        updateAndRefresh(space);
    }
        
//    public void disableAdmin(Space space){
//        changeAdminStatus(space, UserStatus.DISABLED);
//
//    }
//    
//    public void enableAdmin(Space space){
//         changeAdminStatus(space, UserStatus.ENABLED);
//      
//    }
//    
//    private void changeAdminStatus(Space space,UserStatus status){
//        adminService.findBySpace(space)
//                .flatMap(adm -> adminService.changeAdminStatus(adm, status))
//                .ifPresent(this::updateAdmin_);
//    }
//    
//    private Optional<Admin> updateAdmin_(Admin admin){
//       try {
//           return  adminService.updateAdmin(admin);
//       } catch (AdminLoginExistException | NotMatchingPasswordAndConfirmation ex) {
//           addGlobalErrorMessage(ex.getMessage());
//       }
//       
//       return Optional.empty();
//    }
    
//    public boolean rendererAssociateAdminLink(Space space){
////       return adminService.findBySpace(space).isEmpty();
//    }
//    
//    public boolean rendererRemoveAssociationLink(Space space){
//        return adminService.findBySpace(space).isPresent();
//    }
   
    public boolean rendererEnableSpaceLink(Space space){
        return space.getStatus().equals(SpaceStatus.DISABLED);
    }
    
     public boolean rendererDisableSpaceLink(Space space){
        return space.getStatus().equals(SpaceStatus.ENABLED);
    }
    
//    public boolean rendererEnableAdminLink(Space space){
//        return adminService.findBySpace(space).stream()
//                .filter(adm -> adm.getStatus().equals(UserStatus.DISABLED))
//                .count() > 0;
//    }
    
//    public boolean rendererDisableAdminLink( Space space){
//        return adminService.findBySpace(space).stream()
//                .filter(adm -> adm.getStatus().equals(UserStatus.ENABLED))
//                .count() > 0;
//    }

    public void handleEditSpaceReturn(SelectEvent event){
       LOG.log(Level.INFO, "---> RETURN FROM HANDLE ADD TENZNT...");
       initSpaces();
       selectedEntity = (Space)event.getObject();
    }
  
    public void handleSetAdminReturn(SelectEvent event){
       selectedAdmin = (Admin)event.getObject();
       LOG.log(Level.INFO, "--- HANDLE SELECTED ADMIN: {0}", selectedAdmin);
    }
   
    public void updateSpace(Space space){
       LOG.log(Level.INFO, "--- UPDATE SELECTED ADMIN: {0}", selectedAdmin);
//       if(selectedAdmin != null){
//           try {
//               spaceService.updateSpaceAdmin(space, selectedAdmin);
//           } catch (AdminExistException | NotMatchingPasswordAndConfirmation ex) {
//               addGlobalErrorMessage(ex.getMessage());
//           }
//       }
   }
   
//   public String retrieveAdmin(Space space){
//     return adminService.findBySpace(space)
//             .map(Admin::getName).orElse("");
//     
//   }
//   
//   public String retrieveAdminLogin(Space space){
//     return adminService.findBySpace(space)
//             .map(Admin::getLogin).orElse("");
//   }
   
//   public String retrieveAdminStatus(Space space){
//    return adminService.findBySpace(space)
//             .map(Admin::getStatus).map(Object::toString).orElse("");
//     
//   }
 
    public boolean rendererAction( Admin admin){
        return selectedAdmins.contains(admin);
    }
         
    public void check( Admin admin){   
       selectedAdmins.add(admin);
        
    }
    
    public void uncheck( Admin admin){
       selectedAdmins.remove(admin);
   }
    
    public void provideSelectedSpace(Space space){
        selectedEntity = space;
    }

    public void handleDialogClose(CloseEvent closeEvent){
        initSpaces();
    }
      
    public Admin getSelectedAdmin() {
        return selectedAdmin;
    }

    public Set<Admin> getSelectedAdmins() {
        return selectedAdmins;
    }

    public ContextIdParamKey getContextIdParamKey() {
        return contextIdParamKey;
    }

    @Override
    protected String deleteViewId() {
        return ViewID.DELETE_SPACE_DIALOG.id();
    }

}
