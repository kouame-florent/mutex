/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.search.valueobject.FileInfo;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author florent
 */
public interface TikaContentService {

    Optional<String> getRawContent(FileInfo fileInfo, Map<String, String> metas);
    
}
