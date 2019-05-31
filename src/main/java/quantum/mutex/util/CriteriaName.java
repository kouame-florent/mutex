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
public enum CriteriaName {
    CONTENT("content"),
    DATE_RANGE("date_range"),
    SIZE_RANGE("size_range"), 
    OWNER("owner");
    
    private final String value;
    
    private CriteriaName(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
