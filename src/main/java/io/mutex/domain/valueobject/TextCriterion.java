/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.domain.valueobject;

import java.util.Optional;
import lombok.Getter;



/**
 *
 * @author Florent
 */
public class TextCriterion implements SearchCriterion{
    
    @Getter
    private final String searchText;

    private TextCriterion(String searchText) {
        this.searchText = searchText;
    }
    
    public static Optional<TextCriterion> of(String searchText){
        return isValid(searchText) ? Optional.ofNullable(new TextCriterion(searchText)): 
                 Optional.empty() ;
    }
  
    private static boolean isValid(String text) {
        return (text != null) && !text.isBlank();
    }
    
    public String searchText(){
        return searchText;
    }

}
