/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.util;

/**
 *
 * @author Florent
 */
public enum VirtualPageProperty {
    PAGE_UUID("page_uuid"),
    FILE_UUID("file_uuid"),
    FILE_NAME("file_name"),
    CONTENT("content"),
    PAGE_INDEX("page_index"),
    TOTAL_PAGE_COUNT("total_page_count");
    
    private final String value;
    
    private VirtualPageProperty(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
