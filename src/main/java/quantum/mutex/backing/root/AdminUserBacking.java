/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.root;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.domain.AdminUser;
import quantum.mutex.domain.dao.AdminUserDAO;
import quantum.mutex.domain.dao.TenantDAO;

/**
 *
 * @author Florent
 */
@Named(value = "adminUserBacking")
@ViewScoped
public class AdminUserBacking extends BaseBacking implements Serializable{
    
    
    @Inject AdminUserDAO adminUserDAO;
    @Inject TenantDAO tenantDAO;
    
    private AdminUser selectedAdminUser;
    private List<AdminUser> adminUsers ;
    
   @PostConstruct
   public void init(){
       initAdminUsers();
   }
   
   private void initAdminUsers(){
       adminUsers = adminUserDAO.findAll();
   }
   
   
   
   public void openAddAdminUserDialog(){
        Map<String,Object> options = getDialogOptions(45, 55,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-administrator-dlg", options, null);
   }
   
   public String retrieveTenant(AdminUser adminUser){
      return (adminUser.getTenant() != null) ? adminUser.getTenant().getName() : "";
   }

   public void handleAddAdminUserReturn(SelectEvent event){
       initAdminUsers();
       selectedAdminUser = (AdminUser)event.getObject();
   }
   

    public List<AdminUser> getAdminUsers() {
        return adminUsers;
    }

    public AdminUser getSelectedAdminUser() {
        return selectedAdminUser;
    }

    public void setSelectedAdminUser(AdminUser selectedAdminUser) {
        this.selectedAdminUser = selectedAdminUser;
    }

   
   
}
