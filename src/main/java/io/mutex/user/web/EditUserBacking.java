/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;


import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.entity.Searcher;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.RoleDAO;
import io.mutex.user.repository.UserRoleDAO;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.UserLoginExistException;
import io.mutex.user.service.UserRoleService;
import io.mutex.user.service.SearcherService;


/**
 *
 * @author Florent
 */
@Named(value = "editUserBacking")
@ViewScoped
public class EditUserBacking extends QuantumEditBacking<Searcher> implements Serializable{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(EditUserBacking.class.getName());
       
    private final ContextIdParamKey userParamKey = ContextIdParamKey.USER_UUID;

    @Inject SearcherService standardUserService;
    @Inject GroupDAO groupDAO;
    @Inject UserRoleDAO userRoleDAO;
    @Inject RoleDAO roleDAO;
    @Inject UserRoleService userRoleService;
 
    private Searcher currentUser;
     
    @Override
    public void viewAction(){
        viewState = initViewState(entityUUID);
        currentUser = initEntity(entityUUID);
        currentUser = presetConfirmPassword(currentUser);
  
    }
    
    @Override
    protected Searcher initEntity(String entityUUID) {
         return Optional.ofNullable(entityUUID)
                .flatMap(standardUserService::findByUuid)
                .map(this::presetConfirmPassword)
                .orElseGet(() -> new Searcher());
    }

    @Override
    public void edit() {
         switch(viewState){
             case CREATE:
             {
                 try {
                     standardUserService.create(currentUser).ifPresent(this::returnToCaller);
                 } catch (NotMatchingPasswordAndConfirmation | UserLoginExistException ex) {
                     addGlobalErrorMessage(ex.getMessage());
                 }
             }
             break;
             case UPDATE:
             {
                 try {
                     standardUserService.update(currentUser).ifPresent(this::returnToCaller);
                 } catch (NotMatchingPasswordAndConfirmation ex) {
                     addGlobalErrorMessage(ex.getMessage());
                 }
             }
             break;


         }
    }

    private Searcher presetConfirmPassword(Searcher standardUser){
       standardUser.setConfirmPassword(standardUser.getPassword());
       return standardUser;
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

}
