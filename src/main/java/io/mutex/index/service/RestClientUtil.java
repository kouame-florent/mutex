/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;

/**
 *
 * @author florent
 */
public interface RestClientUtil {

    Optional<CreateIndexRequest> addSource(CreateIndexRequest request, String source);

    Optional<CreateIndexResponse> createIndex(CreateIndexRequest request);

    Optional<AcknowledgedResponse> deleteIndex(DeleteIndexRequest request);

    boolean exists(String index);

    Optional<AnalyzeRequest> getAnalyzeRequest();

    Optional<AnalyzeRequest> getAnalyzeRequest(String index);

    RestHighLevelClient getElClient();

    Client getRsClient();

    @PostConstruct void init();
    
    @PreDestroy void close();
    
    Optional<Response> get(String target, MediaType mediaType);

    Optional<Response> post(String target, Entity<?> entity, MultivaluedMap headers);

    Optional<Response> put(String target, Entity<?> entity, MultivaluedMap headers);
    
}
