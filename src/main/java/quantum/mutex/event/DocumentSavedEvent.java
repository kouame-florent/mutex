/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.event;

import java.io.Reader;
import java.nio.file.Path;
import quantum.mutex.domain.Document;

/**
 *
 * @author Florent
 */
public class DocumentSavedEvent {
    
    private Document document;
    private Path filePath;

    public DocumentSavedEvent(Document document, Path filePath) {
        this.document = document;
        this.filePath = filePath;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    
    
}
