/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.root;


import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.dao.AdminUserDAO;

/**
 *
 * @author Florent
 */
@Named("chooseAdminBacking")
@ViewScoped
public class ChooseAdminBacking implements Serializable{

    @Inject AdminUserDAO adminUserDAO;
    
    private AdminUser selectedAdminUser;
    private List<AdminUser> adminUsers;
    
    @PostConstruct
    public void init(){
        adminUsers = adminUserDAO.findNotAssignedToTenant();
    }
    
    public void validate(){
        PrimeFaces.current().dialog().closeDynamic(selectedAdminUser);
    }
    
    public boolean rendererAction(AdminUser adminUser){
        return selectedAdminUser == adminUser;
    }
    
     
    public void check(AdminUser adminUser){   
       selectedAdminUser = adminUser;
        
    }
    

    public AdminUser getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public void setSelectedAdminUser(AdminUser selectedAdminUser) {
        this.selectedAdminUser = selectedAdminUser;
    }

    public List<AdminUser> getAdminUsers() {
        return adminUsers;
    }

}
