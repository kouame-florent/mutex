/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;

/**
 *
 * @author Florent
 */
public enum TikaResource {
    
    METADATA("/meta"),
    TIKA("/tika"),
    DETECT("/detect/stream"),
    LANGUAGE("/language/stream"),
    UNPACK("/unpack");
       
    private final String value;

    private TikaResource(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
