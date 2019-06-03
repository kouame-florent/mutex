/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 *
 * @author Florent
 */

@Getter @Setter  @ToString
public class Metadata{
    
    private String uuid = UUID.randomUUID().toString();
    private String inodeUUID;
    private String inodeHash;
    private String fileName;
    private String fileOwner;
    private long fileSize;
    private String fileMimeType;
    private String fileTenant;
    private String fileGroup;
    private long fileCreated;
    private String permissions;
    private String content;

    public Metadata() {
    }

}
