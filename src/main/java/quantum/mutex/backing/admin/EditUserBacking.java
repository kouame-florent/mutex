/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.backing.ViewState;
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

    private static final Logger LOG = Logger.getLogger(EditUserBacking.class.getName());
   
    
    private final ViewParamKey groupParamKey = ViewParamKey.USER_UUID;
    private String userUUID;
    private ViewState viewState = ViewState.CREATE;
    
    @Inject StandardUserDAO standardUserDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserService userService;
    @Inject EncryptionService encryptionService;
    
    @Inject 
    StandardUser currentUser;
    
    private Group selectedGroup;
    
    private List<Group> selectedGroups = new ArrayList<>();      
    private final List<Group> groups = new ArrayList<>();      
    
    public void viewAction(){
        if(userUUID != null){
            viewState = ViewState.UPDATE;
            currentUser = standardUserDAO.findById(UUID.fromString(userUUID));
            retrieveGroups();
            LOG.log(Level.INFO, "--CuRRENT USER UUID: {0}",userUUID);
            LOG.log(Level.INFO, "--CuRRENT USER: {0}",currentUser);
            LOG.log(Level.INFO, "--> SELECTED GROUP SIZE: {0}", selectedGroups.size());
        }
    }
    
    public boolean rendererAction(Group group){
        return selectedGroups.contains(group);
    }
    
     
    public void check(Group group){   
        LOG.log(Level.INFO, "-||- CHECK GROUP : {0}",group.getName());
        selectedGroups.add(group);
        LOG.log(Level.INFO, "--> GROUP IN CONTAINER SIZE: {0}",selectedGroups.size());
    }
    
    public void uncheck(Group group){
        LOG.log(Level.INFO, "-||- UNCHECK GROUP: {0}",group.getName());
        selectedGroups.remove(group);
        LOG.log(Level.INFO, "--> GROUP IN CONTAINER SIZE: {0}", selectedGroups.size());
    }
    
    private void retrieveGroups(){
        
        if(viewState.equals(ViewState.UPDATE)){
            List<Group> userGroups = userGroupDAO.findByUser(currentUser)
                    .stream().map(ug -> ug.getGroup()).collect(Collectors.toList());
            selectedGroups.addAll(userGroups);
        }
        
        groups.clear();
        groups.addAll(groupDAO.findByTenant(getUserTenant().get()));
   }
    
  
    private boolean checkBoxValue;
    
    public void persist(){
        if(viewState.equals(ViewState.CREATE)){
            save();
        }
        if(viewState.equals(ViewState.UPDATE)){
            LOG.log(Level.INFO, "--- UPDATE GROUP.....");
            update();
        }
    }
    
    private void save(){
        if((currentUser != null) && (!selectedGroups.isEmpty()) && isPasswordValid(currentUser)){
            User persistentUser = userService.save(addOtherProperties(currentUser), selectedGroups);
            PrimeFaces.current().dialog().closeDynamic(persistentUser);
        }
    }
    
    private void update(){
        if((currentUser != null) && (!selectedGroups.isEmpty())){
            User persistentUser = userService.update(currentUser, selectedGroups);
            PrimeFaces.current().dialog().closeDynamic(persistentUser);
        }
    }
    
    private User addOtherProperties(User user){
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

    

    public ViewParamKey getGroupParamKey() {
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

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public List<Group> getGroups() {
        return groups;
    }
   
    public boolean isCheckBoxValue() {
        return checkBoxValue;
    }

    public void setCheckBoxValue(boolean checkBoxValue) {
        this.checkBoxValue = checkBoxValue;
    }
    
}
