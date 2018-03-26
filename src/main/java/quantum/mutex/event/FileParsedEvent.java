/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.event;

import java.io.Reader;
import java.nio.file.Path;
import org.apache.tika.metadata.Metadata;

/**
 *
 * @author Florent
 */
public class FileParsedEvent {
    
    private String fileHash;
    private Metadata metadata;
    private Path filePath;
    
    public FileParsedEvent(Metadata metadata, Path filePath,String fileHash) {
        this.metadata = metadata;
        this.filePath = filePath;
        this.fileHash = fileHash;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    
    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
    
    
}
