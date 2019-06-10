/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import lombok.Getter;

/**
 *
 * @author Florent
 */
public class ContentCriteria implements SearchCriteria{
    
    @Getter
    private final String searchText;

    private ContentCriteria(String searchText) {
        this.searchText = searchText;
    }
    
    public static ContentCriteria of(String searchText){
        return new ContentCriteria(searchText);
    }
    
    public static ContentCriteria getDefault(){
        return new ContentCriteria("");
    }
   
    @Override
    public boolean isValid() {
        return (searchText != null) && !searchText.isBlank();
    }
    
    public String searchText(){
        return searchText;
    }
}
