/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.shared.event.GroupCreated;
import io.mutex.shared.event.GroupDeleted;
import io.mutex.user.entity.Group;
import java.util.Optional;
import javax.enterprise.event.Observes;
import javax.validation.constraints.NotNull;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 *
 * @author florent
 */
public interface IndicesService {

    Optional<CreateIndexRequest> addSource(CreateIndexRequest request, XContentBuilder xContentBuilder);

    void createMetadataIndex(@Observes @GroupCreated  @NotNull Group group);

    void createPhraseCompletionIndex(@Observes @GroupCreated @NotNull Group group);

    void createTermCompletionIndex(@Observes @GroupCreated @NotNull Group group);

    void createVirtualPageIndex(@Observes @GroupCreated @NotNull Group group);

    void deleteIndices(@Observes @GroupDeleted @NotNull Group group);
    
}
