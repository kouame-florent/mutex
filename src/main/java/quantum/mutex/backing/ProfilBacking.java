/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
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

    private @Inject FacesContext facesContext;
    private @Inject ExternalContext externalContext;
    
    public String logout(){
        externalContext.invalidateSession();
        return "/protected/user/pages/search-page?faces-redirect=true";
    }
}
