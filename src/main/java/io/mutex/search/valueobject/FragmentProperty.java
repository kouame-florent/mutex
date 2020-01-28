/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

/**
 *
 * @author Florent
 */
public enum FragmentProperty {
    PAGE_UUID("page_uuid"),
    INODE_UUID("inode_uuid"), 
    FILE_NAME("file_name"),
    CONTENT_EN("content.english"),
    CONTENT_FR("content.french"),
    PAGE_INDEX("page_index"),
    TOTAL_PAGE_COUNT("total_page_count");
    
    private final String property;
    
    private FragmentProperty(String property){
        this.property = property;
    }

    public String property() {
        return property;
    }
}
