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

/**
 *
 * @author Florent
 */
@Named(value = "profilBacking")
@SessionScoped
public class ProfilBacking extends BaseBacking implements Serializable{
    
   
    
    public String logout(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/protected/user/pages/search-page?faces-redirect=true";
    }
}
