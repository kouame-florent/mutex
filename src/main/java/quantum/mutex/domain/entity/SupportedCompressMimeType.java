/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.entity;

/**
 *
 * @author Florent
 */
public enum SupportedCompressMimeType {
    APPLICATION_ZLIB("application/zlib"),
    APPLICATION_X_GZIP("application/x-gzip"),
    APPLICATION_X_BZIP2("application/x-bzip2"),
    APPLICATION_GZIP("application/x-bzip2");
    
    private final String value;
    
    private SupportedCompressMimeType(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
