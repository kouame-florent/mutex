/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author root
 */
@Named @RequestScoped
public class Login {
    
    @NotNull
    @Email
    @Getter @Setter
    private String email;
    
    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Getter @Setter
    private String password;
    
    @Inject
    private SecurityContext securityContext;
    
    @Inject
    private ExternalContext externalContext;
    
    @Inject
    private FacesContext facesContext;
    
    public void submit() {
    
    }
}
