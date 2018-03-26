/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Florent
 */
@Dependent
public class BaseBacking implements Serializable{
    
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
    
}
