/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.entity.Inode;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.user.entity.Group;
import java.util.List;

/**
 *
 * @author florent
 */
public interface MutexPageService {

    List<VirtualPage> buildVirtualPages(String rawContent, String fileName, Inode inode,Group group);

    String contentMappingProperty();

    void indexVirtualPages(List<VirtualPage> virtualPages, Group group);

    String trigramMappingProperty();
    
}
