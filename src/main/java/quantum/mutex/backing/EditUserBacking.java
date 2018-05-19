/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.collections4.CollectionUtils;
import org.primefaces.PrimeFaces;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.StandardUser;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserStatus;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.StandardUserDAO;
import quantum.mutex.service.EncryptionService;
import quantum.mutex.service.UserService;


/**
 *
 * @author Florent
 */
@Named(value = "editUserBacking")
@ViewScoped
public class EditUserBacking extends BaseBacking implements Serializable{
    
    private final DialogParamKey groupParamKey = DialogParamKey.USER_UUID;
    private String userUUID;
    private ViewState viewState = ViewState.CREATE;
    
    @Inject StandardUserDAO standardUserDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserService userService;
    @Inject EncryptionService encryptionService;
    
    @Inject @RequestScoped
    private StandardUser currentUser;
    
    private List<Group> selectedGroups;      
    
    public void viewAction(){
        if(userUUID != null){
            viewState = ViewState.UPDATE;
            currentUser = standardUserDAO.findById(UUID.fromString(userUUID));
        }
    }
    
    public List<Group> getAvailableGroups(){
        
        if(viewState.equals(ViewState.CREATE)){
             return groupDAO.findByTenant(getUserTenant().get());
        }
        
        if(viewState.equals(ViewState.UPDATE)){
            List<Group> tenantGroups = groupDAO.findByTenant(getUserTenant().get());
            List<Group> userGroups = userGroupDAO.findByUser(currentUser)
                    .stream().map(ug -> ug.getGroup()).collect(Collectors.toList());

            return new ArrayList<>(CollectionUtils.subtract(tenantGroups, userGroups));
        }
        
        
        return new ArrayList<>();
   }
    
    public void persist(){
        if((currentUser != null) && (selectedGroups != null) && isPasswordValid(currentUser)){
            User persistentUser = userService.save(finalyzeUser(currentUser), selectedGroups);
            PrimeFaces.current().dialog().closeDynamic(persistentUser);
        }
    }
    
    private User finalyzeUser(User user){
        user.setTenant(getUserTenant().get());
        user.setPassword(encryptionService.hash(currentUser.getPassword()));
        user.setStatus(UserStatus.ENABLED);
        return user;
    }
    
    private boolean isPasswordValid(User user){
        boolean result = user.getPassword().equals(user.getConfirmPassword());
        if(!result){
            addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
        }
        return result;
    }
    
    public void close(){
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public StandardUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(StandardUser currentUser) {
        this.currentUser = currentUser;
    }

    

    public DialogParamKey getGroupParamKey() {
        return groupParamKey;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public ViewState getViewState() {
        return viewState;
    }

    public List<Group> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(List<Group> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }
    
    
    
}
