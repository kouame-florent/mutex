/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 *
 * @author root
 */
@Named @ViewScoped
public class Login implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(Login.class.getName());
    
    @NotNull
    @Email
    private String email;
    
    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @Inject
    private SecurityContext securityContext;
    
    @Inject
    private ExternalContext externalContext;
    
    @Inject
    private FacesContext facesContext;
    
    public void submit() {
        switch (continueAuthentication()) {
            case SEND_CONTINUE:
                facesContext.responseComplete();
                break;
            case SEND_FAILURE:
                facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Login has failed", null));
                break;
            case SUCCESS:
                facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Login succeed", null));
                break;
            case NOT_DONE:
                // Doesn’t happen here
        }
    }
        
    private AuthenticationStatus continueAuthentication() {
        LOG.log(Level.INFO, "--> AUTHENTICATE...");
        LOG.log(Level.INFO, "--> EMAIL: {0}",email);
        LOG.log(Level.INFO, "--> PASSWORD: {0}",password); 
        return securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            AuthenticationParameters.withParams().credential(
                new UsernamePasswordCredential(email, password))
        );
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
    
}
