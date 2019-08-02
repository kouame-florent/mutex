/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;

/**
 *
 * @author Florent
 */
public class TextCriterion implements SearchCriterion{
    
    private String text;

    public TextCriterion(String text) {
        this.text = text;
    }
    
    public static TextCriterion of(String text){
        return new TextCriterion(text);
    }
    
    public static TextCriterion getDefault(){
        return new TextCriterion("");
    }

    @Override
    public boolean isValid() {
        return (text != null);
    }
    
}
