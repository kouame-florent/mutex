/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import quantum.mutex.domain.entity.Group;


/**
 *
 * @author Florent
 */

@Getter @Setter
public class FileInfo {
    private final String fileName;
    private final long fileSize;
    private final Path filePath;
    private final String fileHash;
    private final Group fileGroup;
//    private final String fileContentType;
//    private String fileLanguage;
//    private Metadata fileMetadata;
//    private String rawContent;
//    private Inode inode;

    public FileInfo(String fileName, long fileSize, Path filePath, 
            String fileHash, Group ownerGroup) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileHash = fileHash;
        this.fileGroup = ownerGroup;
    }

   

    
}
