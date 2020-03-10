/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.entity.Inode;
import io.mutex.search.valueobject.FileInfo;
import io.mutex.search.valueobject.Metadata;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author florent
 */
public interface TikaMetadataService {

    Metadata buildMutexMetadata(FileInfo fileInfo, Inode inode, Map<String, String> metadatas);

    Optional<String> getContentType(Map<String, String> metadatas);

    Optional<String> getLanguage(Map<String, String> metadatas);

    Map<String, String> getMetadatas(Path filePath);

    String getMetadatasAsString(Map<String, String> metadatas);
    
}
