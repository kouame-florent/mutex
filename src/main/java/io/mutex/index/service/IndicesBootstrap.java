/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.valueobject.IndexMapping;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.search.service.ElasticMappingConfigLoader;
import io.mutex.shared.event.GroupCreated;
import io.mutex.user.entity.Group;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;

/**
 *
 * @author florent
 */
@Singleton
@Startup
public class IndicesBootstrap {

    private static final Logger LOG = Logger.getLogger(IndicesBootstrap.class.getName());
    
    @Inject ElasticMappingConfigLoader mappingConfigLoader;
    @Inject IndexNameUtils queryUtils;
    @Inject ElApiLogUtil elasticApiUtils;
    @Inject RestClientUtil restClientUtils;
    
    @PostConstruct
    private void init(){
        tryCreateUtilIndex();
    }
    
    public void tryCreateUtilIndex(){
        if(!restClientUtils.exists(IndexNameSuffix.MUTEX_UTIL.suffix())){
            LOG.log(Level.INFO, "... CREATING UTIL INDEX ...");
            Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.UTIL.mapping());
            Optional<String> target = buildUtilIndexUri();
            Optional<CreateIndexRequest> rRequest = target.map(t -> new CreateIndexRequest(t));
            
            Optional<CreateIndexRequest> requestWithContent = 
                    rRequest.flatMap(r -> json.flatMap(j -> restClientUtils.addSource(r, j)));
            
            requestWithContent.ifPresent(r -> elasticApiUtils.logJson(r));
            Optional<CreateIndexResponse> rResponse = requestWithContent.flatMap(r -> restClientUtils.createIndex(r));

            rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
                   
        }
        
    }
     
    private Optional<String> buildUtilIndexUri(){
        String target = IndexNameSuffix.MUTEX_UTIL.suffix();
        LOG.log(Level.INFO, "--> INDEX NAME: {0}",target);
        return Optional.of(target);
    }
    
//    private boolean exists(String index){
//        try {
//            GetIndexRequest request = new GetIndexRequest(index);
//            return apiClientUtils.getElClient()
//                    .indices().exists(request, RequestOptions.DEFAULT);
//        } catch (IOException ex) {
//            Logger.getLogger(ManageIndicesService.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//    }
//    
//    private Optional<CreateIndexResponse>  createIndex(CreateIndexRequest request){
//        LOG.log(Level.INFO,"---- CREATING INDEX ----");
//        try {
//            return Optional.ofNullable(apiClientUtils
//                            .getElClient().indices().create(request, RequestOptions.DEFAULT));
//        } catch (Exception ex) {
//            Logger.getLogger(ManageIndicesService.class.getName()).log(Level.SEVERE, null, ex);
//            return Optional.empty();
//        }
//    }
//    
//    private Optional<CreateIndexRequest> addSource(CreateIndexRequest request,String source){
//        request.source(source, XContentType.JSON);
////        request.mapping(IndexName.COMPLETION.value(), xContentBuilder);
//        return Optional.of(request);
//    }
    
}
