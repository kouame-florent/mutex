/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.util;

/**
 *
 * @author Florent
 */
public enum MetaFragmentProperty {
    META_UUID("meta_uuid"),
    INODE_UUID("inode_uuid"), 
    FILE_NAME("file_name"), 
    FILE_SIZE("file_size"),
    FILE_OWNER("file_owner"),
    FILE_GROUP("file_group"),
    FILE_CREATED("file_created"),
    FILE_MIME_TYPE("file_mime_type"),
    CONTENT("content");
    
    private final String value;
    
    private MetaFragmentProperty(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
