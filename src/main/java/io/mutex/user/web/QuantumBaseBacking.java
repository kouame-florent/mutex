/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Space;
import io.mutex.user.repository.UserDAO;
import io.mutex.user.repository.UserGroupDAO;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.entity.User;
import io.mutex.index.valueobject.Constants;


/**
 *
 * @author Florent Kouamé
 */

public abstract class QuantumBaseBacking implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(QuantumBaseBacking.class.getName());
   
    private @Inject UserDAO userDAO;
    private @Inject UserGroupDAO userGroupDAO;
    
    private @Inject FacesContext facesContext;
    private @Inject ExternalContext externalContext;
            
    public void addGlobalMessage(FacesMessage msg){
        facesContext.addMessage(null, msg);
    }
    
    public void addGlobalMessage(String message,FacesMessage.Severity severity){
        FacesMessage msg = new FacesMessage(severity,
                        message ,"");
       facesContext.addMessage(null, msg);
       
    }
    
    public void addGlobalInfoMessage(String message){
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        message ,"");
       facesContext.addMessage(null, msg);
    }
    
    public void addGlobalErrorMessage(String message){
       FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        message ,"");
       facesContext.addMessage(null, msg);
    }
    
    public void addGlobalWarningMessage(String message){
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        message ,"");
        facesContext.addMessage(null, msg);
    }
       
    public void addMessage(String clientId,FacesMessage msg){
        facesContext.addMessage(clientId, msg);
    }
    
    public void addMessageFromResourceBundle(String viewId,String bundleMessageKey,FacesMessage.Severity severity){
       
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        if(bundle != null){
             String message = bundle.getString(bundleMessageKey);
             facesContext.addMessage(viewId, new FacesMessage(severity, message, ""));
        }
   }
    
    protected Optional<String> getAuthenticatedUserLogin(){
       Optional<String> oName = Optional.ofNullable(externalContext.getUserPrincipal().getName());
       return oName.or(() -> Optional.of(Constants.ANONYMOUS_USER_PRINCIPAL_NAME));
    }
    
    public Optional<User> getAuthenticatedUser(){
        return getAuthenticatedUserLogin()
                .flatMap(userDAO::findByLogin);
    }
    
    public String getUserlogin(){
        return getAuthenticatedUserLogin().orElseGet(() -> "");
    }
    
//    public Optional<Space> getUserSpace(){
//        LOG.log(Level.INFO, "--> AUTH USER LOGIN: {0}", getAuthenticatedUserLogin());
//        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
//                    .map(u -> u.getGroup().getSpace());
//                    
//    }
    
//    public String getUserSpaceName(){
//       return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
//                    .map(u -> u.getGroup().getSpace())
//                    .map(t -> t.getName())
//                    .orElseGet(() -> "");
//    }
    
//    public Optional<Group> getUserPrimaryGroup(){
//        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
//                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
//                    .map(ug -> ug.getGroup());
//    }
//     
//    public String getUserPrimaryGroupName(){
//        return getAuthenticatedUserLogin().flatMap(userDAO::findByLogin)
//                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
//                    .map(ug -> ug.getGroup().getName())
//                    .orElseGet(() -> "");
//    }
// 
    protected Map<String,Object> getDialogOptions(int widthPercent,int heightPercent,boolean closable){
      
        Map<String,Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", false);
        options.put("closable", false);
        options.put("width", widthPercent+"vw");
        options.put("height", heightPercent+"vh");
        options.put("contentWidth", "100%");
        options.put("contentHeight", "95%");
        
        if(closable)options.put("closable", true) ;
        
        return options;
   }
   
   protected Map<String,List<String>> getDialogParams(ContextIdParamKey key,String param){
        Map<String,List<String>> paramsMap = new HashMap<>();
        List<String> paramsList = new ArrayList<>();
        paramsList.add(param);
        paramsMap.put(key.param(), paramsList);
        
        return paramsMap;
   }
    
}
