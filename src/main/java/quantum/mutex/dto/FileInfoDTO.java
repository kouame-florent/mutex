/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.dto;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import quantum.mutex.domain.DocumentFile;
import quantum.mutex.domain.DocumentMetadata;

/**
 *
 * @author Florent
 */
public class FileInfoDTO {
    
    private String fileName;
    private long fileSize;
    private Path filePath;
    private String fileHash;
    private String fileContentType;
    private String fileLanguage;
    private final List<DocumentMetadata> fileMetadatas = new ArrayList<>();
    private DocumentFile document;
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
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

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public List<DocumentMetadata> getFileMetadatas() {
        return fileMetadatas;
    }

    public DocumentFile getDocument() {
        return document;
    }

    public void setDocument(DocumentFile document) {
        this.document = document;
    }

    public String getFileLanguage() {
        return fileLanguage;
    }

    public void setFileLanguage(String fileLanguage) {
        this.fileLanguage = fileLanguage;
    }
    
    
}
