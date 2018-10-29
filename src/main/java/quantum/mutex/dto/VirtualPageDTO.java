/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template mutexFile, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.dto;




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
public class VirtualPageDTO{
    
    private int pageIndex;
    private String content;
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

    public String getMutexFileUUID() {
        return mutexFileUUID;
    }

    public void setMutexFileUUID(String mutexFileUUID) {
        this.mutexFileUUID = mutexFileUUID;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    
    
}
