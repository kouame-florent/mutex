/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;

import lombok.Getter;
import quantum.mutex.domain.type.criterion.SearchCriterion;

/**
 *
 * @author Florent
 */
public class ContentCriterion implements SearchCriterion{
    
    @Getter
    private final String searchText;

    private ContentCriterion(String searchText) {
        this.searchText = searchText;
    }
    
    public static ContentCriterion of(String searchText){
        return new ContentCriterion(searchText);
    }
    
    public static ContentCriterion getDefault(){
        return new ContentCriterion("");
    }
   
    @Override
    public boolean isValid() {
        return (searchText != null) && !searchText.isBlank();
    }
    
    public String searchText(){
        return searchText;
    }
}
