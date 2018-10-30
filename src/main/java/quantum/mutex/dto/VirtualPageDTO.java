/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template mutexFile, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.dto;

import lombok.Getter;
import lombok.Setter;




/**
 *
 * @author Florent
 */
//@NamedQueries({
//    @NamedQuery(
//        name = "VirtualPage.findByMutexFile",
//        query = "SELECT v FROM VirtualPage v WHERE v.mutexFile = :mutexFile " 
//    ),
//   
//})
//@Indexed
//@Table(name = "mx_virtual_page")
//@Entity
@Getter @Setter
public class VirtualPageDTO{
    
    private int pageIndex;
    private String content;
    private String pageHash;
    private String mutexFileUUID;
    
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
