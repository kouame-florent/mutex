/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Named(value = "profilBacking")
@SessionScoped
public class ProfilBacking extends BaseBacking implements Serializable{
    
    
    
    
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
    
    public String logout(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/protected/user/pages/search?faces-redirect=true";
    }
}
