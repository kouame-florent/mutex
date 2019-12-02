/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.search.service;

/**
 *
 * @author Florent
 */
public enum TikaResourceURI {
    
    META("meta"),
    TIKA("tika"),
    DETECT("detect/stream"),
    LANGUAGE("language/stream"),
    UNPACK("unpack");
       
    private final String uri;

    private TikaResourceURI(String uri) {
        this.uri = uri;
    }

    public String uri() {
        return uri;
    }
}
