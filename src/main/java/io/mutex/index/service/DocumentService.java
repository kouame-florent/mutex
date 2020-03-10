/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.search.valueobject.Metadata;
import io.mutex.search.valueobject.PhraseCompletion;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.user.entity.Group;
import java.util.List;

/**
 *
 * @author florent
 */
public interface DocumentService {

    void indexCompletionTerm(List<String> terms, Group group, String fileHash, String inodeUUID, IndexNameSuffix indexNameSuffix);

    void indexMetadata(Metadata metadata, Group group);

    void indexPhraseCompletion(List<PhraseCompletion> phraseCompletions, Group group);

    void indexVirtualPage(List<VirtualPage> virtualPages, Group group);
    
}
