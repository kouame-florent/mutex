/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;

import lombok.Getter;
import quantum.functional.api.Result;

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
    
    public static Result<TextCriterion> of(String searchText){
        return isValid(searchText) ? Result.success(new TextCriterion(searchText)): 
                 Result.empty() ;
    }
  
    private static boolean isValid(String text) {
        return (text != null) && !text.isBlank();
    }
    
    public String searchText(){
        return searchText;
    }

}
