/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

/**
 *
 * @author Florent
 */
public class ContentCriteria implements SearchCriteria{
    
    private final String content;

    public ContentCriteria(String content) {
        this.content = content;
    }
   
    @Override
    public boolean isValid() {
        return !content.isBlank();
    }
    
    public String content(){
        return content;
    }
}
