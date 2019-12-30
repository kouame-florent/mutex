/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.user.entity.AdminUser;
import io.mutex.user.repository.AdminUserDAO;

/**
 *
 * @author Florent
 */
@Named("linkAdminBacking")
@ViewScoped
public class LinkAdminBacking implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject AdminUserDAO adminUserDAO;
    
    private AdminUser selectedAdminUser;
    private List<AdminUser> adminUsers = Collections.EMPTY_LIST;
    
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
