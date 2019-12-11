/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Florent
 */
@Getter @Setter
public class MetaFragment {
    
    private String inodeUUID;
    private String fileName;
    private String fileOwner;
    private long fileSize;
    private String fileMimeType;
    private String fileGroup;
    private LocalDateTime fileCreated;
    private String content;
}
