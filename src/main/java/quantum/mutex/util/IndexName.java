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
public enum IndexName {
    
//    METADATA("metadatas"),
//    VIRTUAL_PAGE("virtual-pages"),
    COMPLETION("completion");
    
    private final String value;
    
    private IndexName(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
