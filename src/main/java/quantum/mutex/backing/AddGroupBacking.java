/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.dao.GroupDAO;

/**
 *
 * @author Florent
 */
@Named(value = "addGroupBacking")
@ViewScoped
public class AddGroupBacking extends BaseBacking implements Serializable{
    
    @Inject GroupDAO groupDAO;
    
    @Inject @RequestScoped
    private Group currentGroup; 
    
    public void save(){
        if(getUserTenant().isPresent()){
            currentGroup.setTenant(getUserTenant().get());
            Group persistentGroup = groupDAO.makePersistent(currentGroup);
            PrimeFaces.current().dialog().closeDynamic(persistentGroup);
        }
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }

   
    
    
}
