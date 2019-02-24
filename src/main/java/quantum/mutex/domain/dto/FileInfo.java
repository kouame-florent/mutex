/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Inode;

/**
 *
 * @author Florent
 */
@Getter
@Setter
public class FileInfo {
    private String fileName;
    private long fileSize;
    private Path filePath;
    private String fileHash;
    private String fileContentType;
    private String fileLanguage;
    private final List<Metadata> fileMetadatas = new ArrayList<>();
    private String rawContent;
    private Inode inode;
    private Group group;
    
}
