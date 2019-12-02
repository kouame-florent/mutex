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
public enum MutexUtilAnalyzer {
    
    SHINGLE("mutex_shingle"),
    COMPLETION_FRENCH("mutex_completion_french"),
    COMPLETION_ENGLISH("mutex_completion_english");
    
    
    private final String value;
    
    private MutexUtilAnalyzer(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
