/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import quantum.mutex.util.RestClientUtil;
import quantum.mutex.util.QueryUtils;
import java.io.IOException;
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
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
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
        
        Result<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.METADATA.value());
        Result<String> target = queryUtils.indexName(group,IndexNameSuffix.METADATA.value());
        Result<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.forEach(r -> elasticApiUtils.logJson(r));
        
       // Result<XContentBuilder> rContentBuilder = createTermCompleteIndexMapping();
       
        request.forEach(r -> elasticApiUtils.logJson(r));
               
        Result<CreateIndexRequest> requestWithSource = 
                request.flatMap(r -> json.flatMap(j -> addSource(r, j)));
        requestWithSource.forEach(r -> elasticApiUtils.logJson(r));
        Result<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> doCreateIndex(r));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                .forEach(e -> LOG.log(Level.INFO, "{0}", e));
    }
    
     
//    private MultivaluedMap<String,Object> headers(){
//        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
//        headers.add("Accept", "application/json");
//        return headers;
//    }
    
    public void createVirtualPageIndex( Group group){
        
        Result<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.VIRTUAL_PAGE.value());
        Result<String> target = queryUtils.indexName(group,IndexNameSuffix.VIRTUAL_PAGE.value());
        Result<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.forEach(r -> elasticApiUtils.logJson(r));
        
//        Result<XContentBuilder> rContentBuilder = createTermCompleteIndexMapping();
        request.forEach(r -> elasticApiUtils.logJson(r));
                
        Result<CreateIndexRequest> requestWithSource = 
                request.flatMap(r -> json.flatMap(j -> addSource(r, j)));
        requestWithSource.forEach(r -> elasticApiUtils.logJson(r));
        Result<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> doCreateIndex(r));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                .forEach(e -> LOG.log(Level.INFO, "{0}", e));
      }
    
    public void createTermCompletionIndex( Group group){
        Result<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.TERM_COMPLETION.value());
        Result<String> target = queryUtils.indexName(group,IndexNameSuffix.TERM_COMPLETION.value());
        Result<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.forEach(r -> elasticApiUtils.logJson(r));
                 
        Result<CreateIndexRequest> requestWithSource = 
                request.flatMap(r -> json.flatMap(j -> addSource(r, j)));
        requestWithSource.forEach(r -> elasticApiUtils.logJson(r));
        Result<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> doCreateIndex(r));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                .forEach(e -> LOG.log(Level.INFO, "{0}", e));
    }
    
    public void createPhraseCompletionIndex( Group group){
        Result<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.PHRASE_COMPLETION.value());
        Result<String> target = queryUtils.indexName(group,IndexNameSuffix.PHRASE_COMPLETION.value());
        Result<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.forEach(r -> elasticApiUtils.logJson(r));
                 
        Result<CreateIndexRequest> requestWithSource = 
                request.flatMap(r -> json.flatMap(j -> addSource(r, j)));
        requestWithSource.forEach(r -> elasticApiUtils.logJson(r));
        Result<CreateIndexResponse> rResponse = requestWithSource.flatMap(r -> doCreateIndex(r));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                .forEach(e -> LOG.log(Level.INFO, "{0}", e));
    }
    
     public void tryCreateUtilIndex(){
        if(!exists(IndexNameSuffix.MUTEX_UTIL.value())){
            LOG.log(Level.INFO, "... CREATING UTIL INDEX ...");
            Result<String> json =  mappingConfigLoader.retrieveIndexMapping(IndexMapping.UTIL.value());
            Result<String> target = buildUtilIndexUri();
            Result<CreateIndexRequest> rRequest = target.map(t -> new CreateIndexRequest(t));
            
            Result<CreateIndexRequest> requestWithContent = 
                    rRequest.flatMap(r -> json.flatMap(j -> addSource(r, j)));
            
            requestWithContent.forEach(r -> elasticApiUtils.logJson(r));
            Result<CreateIndexResponse> rResponse = requestWithContent.flatMap(r -> doCreateIndex(r));

            rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                    .forEach(e -> LOG.log(Level.INFO, "{0}", e));
        }
        
    }
 
    private Result<CreateIndexResponse>  doCreateIndex(CreateIndexRequest request){
        LOG.log(Level.INFO,"---- CREATING INDEX ----");
        try {
            return Result.success(apiClientUtils
                            .getElClient().indices().create(request, RequestOptions.DEFAULT));
        } catch (Exception ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
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
    
    private Result<CreateIndexRequest> addSource(CreateIndexRequest request,
            XContentBuilder xContentBuilder){
        request.source(xContentBuilder);
//        request.mapping(IndexName.COMPLETION.value(), xContentBuilder);
        return Result.of(request);
    }
    
    private Result<CreateIndexRequest> addSource(CreateIndexRequest request,String source){
        request.source(source, XContentType.JSON);
//        request.mapping(IndexName.COMPLETION.value(), xContentBuilder);
        return Result.of(request);
    }
    
//    private Result<XContentBuilder> createTermCompleteIndexMapping(){
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
//            return Result.success(builder);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
//            return Result.failure(ex);
//        }
//        
//    }
//    
    
//     private Result<XContentBuilder> createPhraseCompleteIndexMapping(){
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
//            return Result.success(builder);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
//            return Result.failure(ex);
//        }
//        
//    }
   
    private Result<String> buildUtilIndexUri(){
        String target = IndexNameSuffix.MUTEX_UTIL.value();
        LOG.log(Level.INFO, "--> INDEX NAME: {0}",target);
        return Result.of(target);
    }
    
    private final Effect<Response> close = r -> {
        if(r != null) r.close();
    };
    
}
