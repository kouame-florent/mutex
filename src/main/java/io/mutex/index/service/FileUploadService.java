/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.entity.Inode;
import io.mutex.search.valueobject.FileInfo;
import java.util.Map;
import javax.ejb.Asynchronous;

/**
 *
 * @author florent
 */
public interface FileUploadService {

    @Asynchronous void indexContent(FileInfo fileInfo);

    void indexMetadatas(Inode inode, Map<String, String> tikaMetas, FileInfo fileInfo);
    
}
