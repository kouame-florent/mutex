/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import java.util.Optional;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import io.mutex.user.entity.Group;
import io.mutex.search.service.ElasticMappingConfigLoader;
import io.mutex.index.valueobject.IndexMapping;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.shared.event.GroupCreated;
import io.mutex.shared.event.GroupDeleted;
import javax.enterprise.event.Observes;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;


/**
 *
 * @author Florent
 */
@Stateless
public class IndicesServiceImpl implements IndicesService {

    private static final Logger LOG = Logger.getLogger(IndicesServiceImpl.class.getName());
       
    @Inject ElasticMappingConfigLoader mappingConfigLoader;
    @Inject RestClientUtilImpl restClientUtils;
    @Inject IndexNameUtils queryUtils;
    @Inject ElApiLogUtil elasticApiUtils;
    
    @Override
    public void createMetadataIndex(@Observes @GroupCreated @NotNull Group group){
        Optional<CreateIndexRequest> requestWithSource = buildCreateRequest(group,IndexMapping.METADATA,
                IndexNameSuffix.METADATA);
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> restClientUtils.createIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
    }
   
    @Override
    public void createVirtualPageIndex(@Observes @GroupCreated @NotNull Group group){
        Optional<CreateIndexRequest> requestWithSource = buildCreateRequest(group,IndexMapping.VIRTUAL_PAGE,
                IndexNameSuffix.VIRTUAL_PAGE);
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> restClientUtils.createIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
   }
             
    @Override
    public void createTermCompletionIndex(@Observes @GroupCreated @NotNull Group group){
        Optional<CreateIndexRequest> requestWithSource = buildCreateRequest(group,IndexMapping.TERM_COMPLETION,
                IndexNameSuffix.TERM_COMPLETION);
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> restClientUtils.createIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
    }
    
    @Override
    public void createPhraseCompletionIndex(@Observes @GroupCreated @NotNull Group group){
        Optional<CreateIndexRequest> requestWithSource = buildCreateRequest(group,IndexMapping.PHRASE_COMPLETION,
                IndexNameSuffix.PHRASE_COMPLETION);
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> restClientUtils.createIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
    }

    @Override
    public void deleteIndices(@Observes @GroupDeleted @NotNull Group group){
        
       Optional<DeleteIndexRequest> metaRequest = buildDeleteRequest(group, IndexNameSuffix.METADATA);
       Optional<DeleteIndexRequest> termRequest = buildDeleteRequest(group, IndexNameSuffix.TERM_COMPLETION);
       Optional<DeleteIndexRequest> phraseRequest = buildDeleteRequest(group, IndexNameSuffix.PHRASE_COMPLETION);
       Optional<DeleteIndexRequest> vpRequest = buildDeleteRequest(group, IndexNameSuffix.VIRTUAL_PAGE);
       
       metaRequest.flatMap(restClientUtils::deleteIndex);
       termRequest.flatMap(restClientUtils::deleteIndex);
       phraseRequest.flatMap(restClientUtils::deleteIndex);
       vpRequest.flatMap(restClientUtils::deleteIndex);
    }
            
    private Optional<CreateIndexRequest>  buildCreateRequest(Group group,IndexMapping indexMapping,
            IndexNameSuffix indexNameSuffix){
        Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(indexMapping.mapping());
        Optional<String> target = queryUtils.getName(group,indexNameSuffix.suffix());
        Optional<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t))
                .flatMap(r -> json.flatMap(j -> restClientUtils.addSource(r, j)));
        request.ifPresent(r -> elasticApiUtils.logJson(r));
        
        return request;
    }
    
    private Optional<DeleteIndexRequest> buildDeleteRequest(Group group,
            IndexNameSuffix indexNameSuffix){

        Optional<String> target = queryUtils.getName(group,indexNameSuffix.suffix());
        Optional<DeleteIndexRequest> request = target.filter(restClientUtils::exists).map(DeleteIndexRequest::new);
        return request;
    }

    
    @Override
    public Optional<CreateIndexRequest> addSource(CreateIndexRequest request,
            XContentBuilder xContentBuilder){
        request.source(xContentBuilder);
        return Optional.of(request);
    }

    
}
