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
    
    EDIT_ADMINISTRATOR_DIALOG("edit-administrator-dlg"),
    EDIT_GROUP_DIALOG("edit-group-dlg"),
    EDIT_TENANT_DIALOG("edit-tenant-dlg"),
    EDIT_USER_DIALOG("edit-user-dlg"),
    EDIT_USER_GROUP_DIALOG("edit-user-group-dlg"),
    CHOOSE_ADMIN_DIALOG("choose-admin-dlg");
    
    
    
    private final String id;

    private ViewID(String id){
            this.id = id;
    }
    
    public String id(){
        return id;
    }
    
}
