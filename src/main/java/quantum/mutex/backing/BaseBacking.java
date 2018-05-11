/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import quantum.mutex.domain.GroupType;
import quantum.mutex.domain.Tenant;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Dependent
public class BaseBacking implements Serializable{
    
    @Inject UserDAO userDAO;
    @Inject UserGroupDAO userGroupDAO;
   
    
    public void addGlobalMessage(FacesMessage msg){
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
    
     protected String getAuthenticatedUser(){
        if(FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null){
            
            return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();
            
        }else{
            
            return Constants.ANONYMOUS_USER_PRINCIPAL_NAME;
        }
         
    }
    
    public String getUserlogin(){
        return getAuthenticatedUser();
    }
    
    public Optional<Tenant> getUserTenant(){
        Optional<User> user = userDAO.findByLogin(getAuthenticatedUser());
        return Optional.of(user.get().getTenant());
        
    }
    
    public String getUserTenantName(){
        Optional<User> user = userDAO.findByLogin(getAuthenticatedUser());
        if(user.isPresent()){
            return user.get().getTenant().getName();
        }
        return Constants.ANONYMOUS_TENANT_NAME;
    }
    
    public String getUserPrimaryGroupName(){
        Optional<User> user = userDAO.findByLogin(getAuthenticatedUser());
        
        if(user.isPresent()){
            Optional<UserGroup> optUserGroup 
                    = userGroupDAO.findByUserAndGroupType(user.get(), GroupType.PRIMARY);
            if(optUserGroup.isPresent()){
                return optUserGroup.get().getGroup().getName();
            }
        }
        return "";
    }
    
   protected Map<String,Object> getDialogOptions(int width,int height){
      
        Map<String,Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", false);
        options.put("width", width+"vw");
        options.put("height", height+"vh");
        options.put("contentWidth", "100%");
        options.put("contentHeight", "95%");
        
        return options;
   }
    
}
