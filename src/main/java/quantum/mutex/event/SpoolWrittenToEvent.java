/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.event;

import java.nio.file.Path;

/**
 *
 * @author Florent
 */
public class SpoolWrittenToEvent {
    
    private Path filePath;
    private String fileName;

    public SpoolWrittenToEvent(Path filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
    
}
