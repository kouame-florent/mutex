/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;

/**
 *
 * @author root
 */
public enum SupportedLanguage {
    ENGLISH("en"),
    FRENCH("fr");
    
    private final String language;
    
    private SupportedLanguage(String language){
        this.language = language;
    }
    
    public String language(){ return language;}
}
