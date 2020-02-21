/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

/**
 *
 * @author florent
 */
public class PhraseCompletion {
  
    private String inodeUuid;
    private String content;

    public PhraseCompletion(String inodeUuid, String content) {

        this.inodeUuid = inodeUuid;
        this.content = content;
    }

    public String getInodeUuid() {
        return inodeUuid;
    }

    public void setInodeUuid(String inodeUuid) {
        this.inodeUuid = inodeUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    
    
    
}
