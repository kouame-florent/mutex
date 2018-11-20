/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template mutexFile, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;




/**
 *
 * @author Florent
 */

@Getter @Setter
public class VirtualPageDTO{
    
    private String uuid = UUID.randomUUID().toString();
    private String content;
    private String pageHash;
    private String mutexFileUUID;
    private int pageIndex;
    private String permissions;
    
    public VirtualPageDTO() {
    }
    
    public VirtualPageDTO(String content) {
        this.content = content;
    }

    public VirtualPageDTO(int pageIndex, String content) {
        this.pageIndex = pageIndex;
        this.content = content;
    }

}
