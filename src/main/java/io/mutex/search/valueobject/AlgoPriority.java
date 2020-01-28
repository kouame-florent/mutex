/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

/**
 *
 * @author root
 */
public enum AlgoPriority {
    
    PREFIX_PHRASE_MATCH(2),
    PHRASE_MATCH(1),
    MATCH(0);
    
    private final int priority; 
    
    private AlgoPriority(int priority){
        this.priority = priority;
    }
    
    public int priority(){return priority;}
}
