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
public enum MutexUtilAnalyzer {
    
    SHINGLE("mutex_shingle");
    
    private final String value;
    
    private MutexUtilAnalyzer(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
