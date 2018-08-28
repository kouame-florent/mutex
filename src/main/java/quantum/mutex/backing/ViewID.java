/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

/**
 *
 * @author Florent
 */
public enum ViewID {
    
    EDIT_TENANT_DLG("edit-tenant-dlg"),
    EDIT_ADMINISTRATOR_DLG("edit-administrator-dlg"),
    CHOOSE_ADMIN_DLG("choose-admin-dlg");
    
    
    private final String id;

    private ViewID(String id){
            this.id = id;
    }
    
    public String id(){
        return id;
    }
    
}
