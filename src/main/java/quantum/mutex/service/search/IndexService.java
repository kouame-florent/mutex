/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import quantum.mutex.util.RestClientUtil;
import quantum.mutex.util.QueryUtils;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.config.ElasticMappingConfigLoader;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.IndexMapping;
import quantum.mutex.util.IndexNameSuffix;






/**
 *
 * @author Florent
 */
@Stateless
public class IndexService {

    private static final Logger LOG = Logger.getLogger(IndexService.class.getName());
       
    @Inject ElasticMappingConfigLoader mappingConfigLoader;
    @Inject RestClientUtil apiClientUtils;
    @Inject QueryUtils queryUtils;
    @Inject ElApiUtil elasticApiUtils;
    

    public void createMetadataIndex(Group group){
        
        Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.METADATA.value());
        Optional<String> target = queryUtils.indexName(group,IndexNameSuffix.METADATA.value());
        Optional<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.ifPresent(r -> elasticApiUtils.logJson(r));
        
       // Optional<XContentBuilder> rContentBuilder = createTermCompleteIndexMapping();
       
        request.ifPresent(r -> elasticApiUtils.logJson(r));
               
        Optional<CreateIndexRequest> requestWithSource = 
                request.flatMap(r -> json.flatMap(j -> addSource(r, j)));
        requestWithSource.ifPresent(r -> elasticApiUtils.logJson(r));
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> doCreateIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
                
    }
    
     
//    private MultivaluedMap<String,Object> headers(){
//        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
//        headers.add("Accept", "application/json");
//        return headers;
//    }
    
    public void createVirtualPageIndex( Group group){
        
        Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.VIRTUAL_PAGE.value());
        Optional<String> target = queryUtils.indexName(group,IndexNameSuffix.VIRTUAL_PAGE.value());
        Optional<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.ifPresent(r -> elasticApiUtils.logJson(r));
        
//        Optional<XContentBuilder> rContentBuilder = createTermCompleteIndexMapping();
        request.ifPresent(r -> elasticApiUtils.logJson(r));
                
        Optional<CreateIndexRequest> requestWithSource = 
                request.flatMap(r -> json.flatMap(j -> addSource(r, j)));
        requestWithSource.ifPresent(r -> elasticApiUtils.logJson(r));
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> doCreateIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
    }
                
    
    public void createTermCompletionIndex( Group group){
        Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.TERM_COMPLETION.value());
        Optional<String> target = queryUtils.indexName(group,IndexNameSuffix.TERM_COMPLETION.value());
        Optional<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.ifPresent(r -> elasticApiUtils.logJson(r));
                 
        Optional<CreateIndexRequest> requestWithSource = 
                request.flatMap(r -> json.flatMap(j -> addSource(r, j)));
        requestWithSource.ifPresent(r -> elasticApiUtils.logJson(r));
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> doCreateIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
                
    }
    
    public void createPhraseCompletionIndex( Group group){
        Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.PHRASE_COMPLETION.value());
        Optional<String> target = queryUtils.indexName(group,IndexNameSuffix.PHRASE_COMPLETION.value());
        Optional<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.ifPresent(r -> elasticApiUtils.logJson(r));
                 
        Optional<CreateIndexRequest> requestWithSource = 
                request.flatMap(r -> json.flatMap(j -> addSource(r, j)));
        requestWithSource.ifPresent(r -> elasticApiUtils.logJson(r));
        Optional<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> doCreateIndex(r));
        rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
                
    }
    
     public void tryCreateUtilIndex(){
        if(!exists(IndexNameSuffix.MUTEX_UTIL.value())){
            LOG.log(Level.INFO, "... CREATING UTIL INDEX ...");
            Optional<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.UTIL.value());
            Optional<String> target = buildUtilIndexUri();
            Optional<CreateIndexRequest> rRequest = target.map(t -> new CreateIndexRequest(t));
            
            Optional<CreateIndexRequest> requestWithContent = 
                    rRequest.flatMap(r -> json.flatMap(j -> addSource(r, j)));
            
            requestWithContent.ifPresent(r -> elasticApiUtils.logJson(r));
            Optional<CreateIndexResponse> rResponse = requestWithContent.flatMap(r -> doCreateIndex(r));

            rResponse.ifPresent(r -> elasticApiUtils.logJson(r));
                   
        }
        
    }
 
    private Optional<CreateIndexResponse>  doCreateIndex(CreateIndexRequest request){
        LOG.log(Level.INFO,"---- CREATING INDEX ----");
        try {
            return Optional.ofNullable(apiClientUtils
                            .getElClient().indices().create(request, RequestOptions.DEFAULT));
        } catch (Exception ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
      
    private boolean exists(String index){
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return apiClientUtils.getElClient()
                    .indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
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
    
//    private Optional<XContentBuilder> createTermCompleteIndexMapping(){
//        try {
//            XContentBuilder builder = XContentFactory.jsonBuilder();
//            builder.startObject();
//            {   
//                
//                builder.startObject("properties");
//                {
//                    builder.startObject("page_uuid");
//                    {
//                        builder.field("type", "keyword");
//                    }
//                    builder.endObject();
//                    builder.startObject("term_completion");
//                    {
//                        builder.field("type", "completion");
//                    }
//                    builder.endObject();     
//                }
//                builder.endObject();
//            }
//            builder.endObject();
//            return Optional.success(builder);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
//            return Optional.failure(ex);
//        }
//        
//    }
//    
    
//     private Optional<XContentBuilder> createPhraseCompleteIndexMapping(){
//        try {
//            XContentBuilder builder = XContentFactory.jsonBuilder();
//            builder.startObject();
//            {   
//                
//                builder.startObject("properties");
//                {
//                    builder.startObject("page_uuid");
//                    {
//                        builder.field("type", "keyword");
//                    }
//                    builder.endObject();
//                    builder.startObject("phrase_completion");
//                    {
//                        builder.field("type", "completion");
//                    }
//                    builder.endObject();     
//                }
//                builder.endObject();
//            }
//            builder.endObject();
//            return Optional.success(builder);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
//            return Optional.failure(ex);
//        }
//        
//    }
   
    private Optional<String> buildUtilIndexUri(){
        String target = IndexNameSuffix.MUTEX_UTIL.value();
        LOG.log(Level.INFO, "--> INDEX NAME: {0}",target);
        return Optional.of(target);
    }
    
    private final Consumer<Response> close = r -> {
        if(r != null) r.close();
    };
    
}
