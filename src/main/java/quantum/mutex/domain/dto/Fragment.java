/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Florent
 */
@Getter @Setter
public class Fragment {
    
    private String mutexFileUUID;
    private String content;

    public Fragment() {
    }
    
    public Fragment(String mutexFileUUID, String content) {
        this.mutexFileUUID = mutexFileUUID;
        this.content = content;
    }
    
    
    
}
