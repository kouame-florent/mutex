/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;

import java.io.Serializable;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;

/**
 *
 * @author root
 */
@Dependent
public class LanguageService implements Serializable{
    private SupportedLanguage currentLanguage = SupportedLanguage.FRENCH;

    public void setCurrentLanguage(SupportedLanguage currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
    
    public SupportedLanguage getCurrentLanguage() {
        return currentLanguage;
    }
    
    
    
}
