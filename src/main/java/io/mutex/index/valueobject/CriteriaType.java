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
public enum CriteriaType {
    CONTENT("content"),
    DATE_RANGE("date_range"),
    SIZE_RANGE("size_range"), 
    OWNER("owner");
    
    private final String value;
    
    private CriteriaType(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }
}
