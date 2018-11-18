/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import quantum.functional.api.Result;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Dependent
public class BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(BaseBacking.class.getName());
   
    private @Inject UserDAO userDAO;
    private @Inject UserGroupDAO userGroupDAO;
   
    
    public void addGlobalMessage(FacesMessage msg){
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void addGlobalMessage(String message,FacesMessage.Severity severity){
        FacesMessage msg = new FacesMessage(severity,
                        message ,"");
       FacesContext.getCurrentInstance().addMessage(null, msg);
       
    }
    
    public void addGlobalInfoMessage(String message){
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        message ,"");
       FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void addGlobalErrorMessage(String message){
       FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        message ,"");
       FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void addGlobalWarningMessage(String message){
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        message ,"");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
       
    public void addMessage(String clientId,FacesMessage msg){
        FacesContext.getCurrentInstance().addMessage(clientId, msg);
    }
    
    public void addMessageFromResourceBundle(String viewId,String bundleMessageKey,FacesMessage.Severity severity){
       
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        if(bundle != null){
             String message = bundle.getString(bundleMessageKey);
             FacesContext.getCurrentInstance().addMessage(viewId, new FacesMessage(severity, message, ""));
        }
   }
    
    protected Result<String> getAuthenticatedUser(){
        return Result.of(FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName(), 
                Constants.ANONYMOUS_USER_PRINCIPAL_NAME);
    }
    
    public String getUserlogin(){
        return getAuthenticatedUser().getOrElse(() -> "");
    }
    
    public Result<Tenant> getUserTenant(){
        return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant())
                    .orElse(() ->  Result.empty());
    }
    
    public String getUserTenantName(){
       return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .map(u -> u.getTenant().getName())
                    .getOrElse(() -> "");
    }
    
    public Result<Group> getUserPrimaryGroup(){
        return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
                    .map(ug -> ug.getGroup());
    }
 
    
    public String getUserPrimaryGroupName(){
        return getAuthenticatedUser().flatMap(userDAO::findByLogin)
                    .flatMap(u -> userGroupDAO.findUserPrimaryGroup(u))
                    .map(ug -> ug.getGroup().getName())
                    .getOrElse(() -> "");
    }
 
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
   
   protected Map<String,List<String>> getDialogParams(ViewParamKey key,String param){
        Map<String,List<String>> paramsMap = new HashMap<>();
        List<String> paramsList = new ArrayList<>();
        paramsList.add(param);
        paramsMap.put(key.getValue(), paramsList);
        
        return paramsMap;
   }
    
}
