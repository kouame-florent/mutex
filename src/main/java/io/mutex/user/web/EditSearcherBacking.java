/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;


import io.mutex.index.valueobject.Constants;
import io.mutex.user.entity.Group;
import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.entity.Searcher;
import io.mutex.user.entity.UserGroup;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.RoleDAO;
import io.mutex.user.repository.UserRoleDAO;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.UserLoginExistException;
import io.mutex.user.service.GroupService;
import io.mutex.user.service.UserRoleService;
import io.mutex.user.service.SearcherService;
import io.mutex.user.service.SpaceService;
import io.mutex.user.service.UserGroupService;
import io.mutex.user.valueobject.ViewState;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import static java.util.stream.Collectors.toList;


/**
 *
 * @author Florent
 */
@Named(value = "editSearcherBacking")
@ViewScoped
public class EditSearcherBacking extends QuantumEditBacking<Searcher> implements Serializable{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(EditSearcherBacking.class.getName());
       
    private final ContextIdParamKey userParamKey = ContextIdParamKey.USER_UUID;

    @Inject SearcherService searcherService;
    @Inject GroupDAO groupDAO;
    @Inject UserRoleDAO userRoleDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserRoleService userRoleService;
    @Inject UserGroupService userGroupService;
    @Inject GroupService groupService;
    @Inject SpaceService spaceService; 
    
    private List<Group> selectableGroups = Collections.EMPTY_LIST;
    private List<Group> selectedGroups  = Collections.EMPTY_LIST;; 
 
    private Searcher currentUser;
     
    @Override
    public void viewAction(){
        viewState = initViewState(entityUUID);
        currentUser = initEntity(entityUUID);
        currentUser = presetConfirmPassword(currentUser);
        selectableGroups = initSelectableGroups();
        selectedGroups = initSelectedGroups(viewState, currentUser);
  
    }
    
    @Override
    protected Searcher initEntity(String entityUUID) {
         return Optional.ofNullable(entityUUID)
                .flatMap(searcherService::findByUuid)
                .map(this::presetConfirmPassword)
                .orElseGet(() -> new Searcher());
    }
    
    private List<Group> initSelectableGroups(){
        return groupService.findAll();
    }
    
    private List<Group> initSelectedGroups(ViewState viewState, Searcher searcher){
        if(viewState == ViewState.CREATE){
            Group group = spaceService.findByName(Constants.DEFAULT_SPACE)
                    .flatMap(s -> groupDAO.findBySpaceAndName(s, Constants.DEFAULT_GROUP))
                    .orElseThrow();
            return List.of(group);
        }
        
        if(viewState == ViewState.UPDATE){
            return userGroupService.findByUser(searcher).stream()
                    .map(UserGroup::getGroup).collect(toList());
        }
        
        return List.of();
     
    }

    @Override
    public void edit() {
        selectableGroups
                .forEach(g -> LOG.log(Level.INFO, "--> SELECTABLE GROUP: {0}",g.getName()));
        LOG.log(Level.INFO, "--> SELECTED GROUPS LIST: {0}",selectedGroups);
        
        
        
         switch(viewState){
             case CREATE:
             {
                 try {
                     
                    selectedGroups
                        .forEach(g -> LOG.log(Level.INFO, "-||-> SELECTED GROUPS: {0}",g.getName()));
                     
                     searcherService.create(currentUser).ifPresent(this::returnToCaller);
                     userGroupService.associateGroups(selectedGroups, currentUser);
                 } catch (NotMatchingPasswordAndConfirmation | UserLoginExistException ex) {
                     addGlobalErrorMessage(ex.getMessage());
                 }
             }
             break;
             case UPDATE:
             {
                 try {
                     searcherService.update(currentUser).ifPresent(this::returnToCaller);
                     userGroupService.associateGroups(selectedGroups, currentUser);
                 } catch (NotMatchingPasswordAndConfirmation ex) {
                     addGlobalErrorMessage(ex.getMessage());
                 }
             }
             break;


         }
    }

    private Searcher presetConfirmPassword(Searcher searcher){
       searcher.setConfirmPassword(searcher.getPassword());
       return searcher;
    }

    private void showInvalidPasswordMessage(){
        addMessageFromResourceBundle(null, "user.password.validation.error", 
                FacesMessage.SEVERITY_ERROR);
    }
 
    public Searcher getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Searcher currentUser) {
        this.currentUser = currentUser;
    }

    public ContextIdParamKey getUserParamKey() {
        return userParamKey;
    }

    public List<Group> getSelectableGroups() {
        return selectableGroups;
    }

    public List<Group> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(List<Group> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    
    
    

}
