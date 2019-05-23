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
@Getter
public class MutexSuggestion {
    protected String content;
    protected float score;

    public MutexSuggestion(String content, float score) {
        this.content = content;
        this.score = score;
    }
    
}
