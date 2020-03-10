/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;


import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;




/**
 *
 * @author Florent
 */
@Dependent
public class RestClientUtilImpl implements RestClientUtil {

    private static final Logger LOG = Logger.getLogger(RestClientUtilImpl.class.getName());
    private Client rsClient;

    private RestHighLevelClient elClient;
      
    @PostConstruct
    @Override
    public void init(){
        rsClient = ClientBuilder.newClient();
        elClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
        
    }
    
//    private Optional<String> buildUtilIndexUri(){
//        String target = IndexNameSuffix.MUTEX_UTIL.suffix();
//        LOG.log(Level.INFO, "--> INDEX NAME: {0}",target);
//        return Optional.of(target);
//    }
    
    @Override
    public boolean exists(String index){
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return getElClient()
                    .indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            Logger.getLogger(IndicesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    @Override
    public Optional<CreateIndexResponse>  createIndex(CreateIndexRequest request){
        LOG.log(Level.INFO,"---- CREATING INDEX ----");
        try {
            return Optional.ofNullable(getElClient().indices().create(request, RequestOptions.DEFAULT));
        } catch (Exception ex) {
            Logger.getLogger(IndicesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<AcknowledgedResponse>  deleteIndex(DeleteIndexRequest request){
        LOG.log(Level.INFO,"--> CREATING INDEX ---");
        try {
            return Optional.ofNullable(getElClient().indices().delete(request, RequestOptions.DEFAULT));
        } catch (ElasticsearchException | IOException ex) {
            Logger.getLogger(IndicesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<CreateIndexRequest> addSource(CreateIndexRequest request,String source){
        request.source(source, XContentType.JSON);
//        request.mapping(IndexName.COMPLETION.value(), xContentBuilder);
        return Optional.of(request);
    }
 
  
    @Override
    public Optional<AnalyzeRequest> getAnalyzeRequest(){
        return Optional.of(new AnalyzeRequest());
    }
    
    @Override
     public Optional<AnalyzeRequest> getAnalyzeRequest(String index){
        return Optional.of(new AnalyzeRequest(index)); 
    }
     
     
    @Override
    public Optional<Response> get(String target,MediaType mediaType){
        Response response = rsClient.target(target).request(mediaType).get();
        return Optional.of(response);
    }
    
    @Override
    public Optional<Response> put(String target,Entity<?> entity,MultivaluedMap headers){
//        LOG.log(Level.INFO, "<<-- PUT TARGET: {0}", target);
        Response response = rsClient.target(target).request()
                .headers(headers).put(entity);
        return Optional.of(response);
    }
    
    @Override
    public Optional<Response> post(String target,Entity<?> entity, MultivaluedMap headers){
        LOG.log(Level.INFO, "|<<-- POST TARGET : {0}", target);
        Response response = rsClient.target(target).request()
                .headers(headers).post(entity);
        LOG.log(Level.INFO, "|<<-- POST RESPONSE : {0}", response);
        return Optional.of(response);
    }
    
    @PreDestroy
    @Override
    public void close(){
        rsClient.close();
        try {
            elClient.close();
        } catch (IOException ex) {
            Logger.getLogger(RestClientUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Client getRsClient() {
            return rsClient;
    }

    @Override
    public RestHighLevelClient getElClient() {
            return elClient;
    }

}
