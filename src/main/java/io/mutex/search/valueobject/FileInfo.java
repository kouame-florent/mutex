/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import java.nio.file.Path;
import io.mutex.user.entity.Group;
import javax.validation.constraints.NotNull;


/**
 *
 * @author Florent
 */


public class FileInfo {
    private final String fileName;
    private final long fileSize;
    private final Path filePath;
    private final String fileHash;
    private final Group fileGroup;

    public FileInfo(String fileName, long fileSize, Path filePath, 
            String fileHash,@NotNull Group fileGroup) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileHash = fileHash;
        this.fileGroup = fileGroup;
    }

	public String getFileName() {
		return fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public Path getFilePath() {
		return filePath;
	}

	public String getFileHash() {
		return fileHash;
	}

	public Group getFileGroup() {
		return fileGroup;
	}
    
    

}
