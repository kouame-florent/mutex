/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.valueobject;

/**
 *
 * @author Florent
 */
public enum ViewID {
    
    EDIT_ADMINISTRATOR_DIALOG("edit-administrator-dlg"),
    EDIT_INODE_DIALOG("edit-inode--dlg"),
    EDIT_GROUP_DIALOG("edit-group-dlg"),
    EDIT_SPACE_DIALOG("edit-space-dlg"),
    EDIT_USER_DIALOG("edit-user-dlg"),
    EDIT_USER_GROUP_DIALOG("edit-user-group-dlg"),
    DELETE_ADMINISTRATOR_DIALOG("delete-administrator-dlg"),
    DELETE_GROUP_DIALOG("delete-group-dlg"),
    DELETE_SPACE_DIALOG("delete-space-dlg"),
    DELETE_INODE_DIALOG("delete-inode-dlg"),
    DELETE_USER_DIALOG("delete-user-dlg"),
    DELETE_USER_GROUP_DIALOG("delete-user-group-dlg"),
    FILE_SET_DIALOG("files-set-dlg"),
    UPLOAD_DIALOG("upload-dlg"),
    ADD_ADMIN_DIALOG("add-admin-dlg"),
    DATE_CRITERIA_DIALOG("date-criteria-dlg"),
    SIZE_CRITERIA_DIALOG("size-criteria-dlg"),
    OWNER_CRITERIA_DIALOG("owner-criteria-dlg");
    
    
    
    private final String id;

    private ViewID(String id){
            this.id = id;
    }
    
    public String id(){
        return id;
    }
    
}
