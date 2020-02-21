/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.valueobject.RestClientUtil;
import io.mutex.index.valueobject.QueryUtils;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import io.mutex.user.entity.Group;
import io.mutex.search.service.ElasticMappingConfigLoader;
import io.mutex.index.valueobject.IndexMapping;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.shared.event.GroupCreated;
import io.mutex.shared.event.GroupDeleted;
import javax.enterprise.event.Observes;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;


/**
 *
 * @author Florent
 */
@Stateless
public class ManageIndicesService {

    private static final Logger LOG = Logger.getLogger(ManageIndicesService.class.getName());
       
    @Inject ElasticMappingConfigLoader mappingConfigLoader;
    @Inject RestClientUtil apiClientUtils;
    @Inject QueryUtils queryUtils;
    @Inject ElApiUtil elasticApiUtils;
    
    public void createMetadataIndex(@Observes @GroupCreated @NotNull Group group){
        Optional<CreateIndexRequest> requestWithSource = buildCreateRequest(group,IndexMapping.METADATA,
                IndexNameSuffix.METADATA);
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> createIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
    }
   
    public void createVirtualPageIndex(@Observes @GroupCreated @NotNull Group group){
        Optional<CreateIndexRequest> requestWithSource = buildCreateRequest(group,IndexMapping.VIRTUAL_PAGE,
                IndexNameSuffix.VIRTUAL_PAGE);
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> createIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
   }
             
    public void createTermCompletionIndex(@Observes @GroupCreated @NotNull Group group){
        Optional<CreateIndexRequest> requestWithSource = buildCreateRequest(group,IndexMapping.TERM_COMPLETION,
                IndexNameSuffix.TERM_COMPLETION);
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> createIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
    }
    
    public void createPhraseCompletionIndex(@Observes @GroupCreated @NotNull Group group){
        Optional<CreateIndexRequest> requestWithSource = buildCreateRequest(group,IndexMapping.PHRASE_COMPLETION,
                IndexNameSuffix.PHRASE_COMPLETION);
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> createIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
    }
    
//     public void tryCreateUtilIndex(@Observes @GroupCreated @NotNull Group group){
//        if(!exists(IndexNameSuffix.MUTEX_UTIL.suffix())){
//            LOG.log(Level.INFO, "... CREATING UTIL INDEX ...");
//            Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.UTIL.mapping());
//            Optional<String> target = buildUtilIndexUri();
//            Optional<CreateIndexRequest> rRequest = target.map(t -> new CreateIndexRequest(t));
//            
//            Optional<CreateIndexRequest> requestWithContent = 
//                    rRequest.flatMap(r -> json.flatMap(j -> addSource(r, j)));
//            
//            requestWithContent.ifPresent(r -> elasticApiUtils.logJson(r));
//            Optional<CreateIndexResponse> rResponse = requestWithContent.flatMap(r -> createIndex(r));
//
//            rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
//                   
//        }
//        
//    }
//    
    public void deleteIndices(@Observes @GroupDeleted @NotNull Group group){
        
       Optional<DeleteIndexRequest> metaRequest = buildDeleteRequest(group, IndexNameSuffix.METADATA);
       Optional<DeleteIndexRequest> termRequest = buildDeleteRequest(group, IndexNameSuffix.TERM_COMPLETION);
       Optional<DeleteIndexRequest> phraseRequest = buildDeleteRequest(group, IndexNameSuffix.PHRASE_COMPLETION);
       Optional<DeleteIndexRequest> vpRequest = buildDeleteRequest(group, IndexNameSuffix.VIRTUAL_PAGE);
       
       metaRequest.flatMap(this::deleteIndex);
       termRequest.flatMap(this::deleteIndex);
       phraseRequest.flatMap(this::deleteIndex);
       vpRequest.flatMap(this::deleteIndex);
    }
            
    private Optional<CreateIndexRequest>  buildCreateRequest(Group group,IndexMapping indexMapping,
            IndexNameSuffix indexNameSuffix){
        Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(indexMapping.mapping());
        Optional<String> target = queryUtils.indexName(group,indexNameSuffix.suffix());
        Optional<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t))
                .flatMap(r -> json.flatMap(j -> addSource(r, j)));
        request.ifPresent(r -> elasticApiUtils.logJson(r));
        
        return request;
    }
    
    private Optional<DeleteIndexRequest> buildDeleteRequest(Group group,
            IndexNameSuffix indexNameSuffix){

        Optional<String> target = queryUtils.indexName(group,indexNameSuffix.suffix());
        Optional<DeleteIndexRequest> request = target.filter(this::exists).map(DeleteIndexRequest::new);
//        Optional<DeleteIndexRequest> request = target.map(DeleteIndexRequest::new);
        return request;
    }
    
   
 
    private Optional<CreateIndexResponse>  createIndex(CreateIndexRequest request){
        LOG.log(Level.INFO,"---- CREATING INDEX ----");
        try {
            return Optional.ofNullable(apiClientUtils
                            .getElClient().indices().create(request, RequestOptions.DEFAULT));
        } catch (Exception ex) {
            Logger.getLogger(ManageIndicesService.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
    
    private Optional<AcknowledgedResponse>  deleteIndex(DeleteIndexRequest request){
        LOG.log(Level.INFO,"--> CREATING INDEX ---");
        try {
            return Optional.ofNullable(apiClientUtils
                            .getElClient().indices().delete(request, RequestOptions.DEFAULT));
        } catch (ElasticsearchException | IOException ex) {
            Logger.getLogger(ManageIndicesService.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
      
    private boolean exists(String index){
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return apiClientUtils.getElClient()
                    .indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            Logger.getLogger(ManageIndicesService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private Optional<CreateIndexRequest> addSource(CreateIndexRequest request,
            XContentBuilder xContentBuilder){
        request.source(xContentBuilder);
//        request.mapping(IndexName.COMPLETION.value(), xContentBuilder);
        return Optional.of(request);
    }
    
    private Optional<CreateIndexRequest> addSource(CreateIndexRequest request,String source){
        request.source(source, XContentType.JSON);
//        request.mapping(IndexName.COMPLETION.value(), xContentBuilder);
        return Optional.of(request);
    }
    
//    private Optional<String> buildUtilIndexUri(){
//        String target = IndexNameSuffix.MUTEX_UTIL.suffix();
//        LOG.log(Level.INFO, "--> INDEX NAME: {0}",target);
//        return Optional.of(target);
//    }
//    
    private final Consumer<Response> close = r -> {
        if(r != null) r.close();
    };
    
}
