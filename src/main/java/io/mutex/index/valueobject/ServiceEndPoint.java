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
public enum ServiceEndPoint {
    
    TIKA_BASE_URI("http://172.18.0.2:9998/"),
    ELASTIC_BASE_URI("http://172.18.0.3:9200/");
    
    private final String value;
    
    private ServiceEndPoint(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
