/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import java.util.Optional;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import quantum.mutex.domain.GroupType;
import quantum.mutex.domain.User;
import quantum.mutex.domain.UserGroup;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Named(value = "profilBacking")
@SessionScoped
public class ProfilBacking extends BaseBacking implements Serializable{
    
    @Inject UserDAO userDAO;
    @Inject UserGroupDAO userGroupDAO;
    
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
    
    public String getUserTenant(){
        Optional<User> user = userDAO.findByLogin(getAuthenticatedUser());
        if(user.isPresent()){
            return user.get().getTenant().getName();
        }
        return Constants.ANONYMOUS_TENANT_NAME;
    }
    
    public String getUserPrimaryGroup(){
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
    
    public String logout(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/protected/user/pages/search?faces-redirect=true";
    }
}
