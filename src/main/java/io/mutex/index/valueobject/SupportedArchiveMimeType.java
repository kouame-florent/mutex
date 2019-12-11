/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.valueobject;

/**
 *
 * @author Florent
 */
public enum SupportedArchiveMimeType {
    APPLICATION_X_BZIP("application/x-bzip"),
    APPLICATION_X_BZIP2("application/x-bzip2"),
    APPLICATION_GZIP("application/gzip"),
    APPLICATION_X_GZIP("application/x-gzip"),
    APPLICATION_X_XZ("application/x-xz"),
    APPLICATION_X_TAR("application/x-tar"),
    APPLICATION_X_TIKA_UNIX_DUMP("application/x-tika-unix-dump"),
    APPLICATION_JAVA_ARCHIVE("application/java-archive"),
    APPLICATION_X_7Z_COMPRESSED("application/x-7z-compressed"),
    APPLICATION_X_ARCHIVE("application/x-archive"),
    APPLICATION_X_CPIO("application/x-cpio"),
    APPLICATION_X_ZIP_COMPRESSED("application/x-zip-compressed"),
    APPLICATION_ZIP("application/zip");
 
    
    private final String value;
    
    private SupportedArchiveMimeType(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
