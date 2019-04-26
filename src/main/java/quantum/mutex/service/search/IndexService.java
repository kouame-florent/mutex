/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import quantum.mutex.util.QueryUtils;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Entity;
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
import quantum.mutex.util.ElasticApiUtils;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.ServiceEndPoint;



/**
 *
 * @author Florent
 */
@Stateless
public class IndexService {

    private static final Logger LOG = Logger.getLogger(IndexService.class.getName());
       
    @Inject ElasticMappingConfigLoader mappingConfigLoader;
    @Inject ApiClientUtils apiClientUtils;
    @Inject QueryUtils queryUtils;
    @Inject ElasticApiUtils elasticApiUtils;
    

    public void createMetadataIndex(Group group){
        
        Result<String> json =  mappingConfigLoader.retrieveMetadataIndexMapping();
        Result<String> target = buildMetadataMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
        resp.forEach(close);
    }
    
     
    private MultivaluedMap<String,Object> headers(){
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Accept", "application/json");
        return headers;
    }
    
    public void createVirtualPageIndex(@NotNull Group group){
        Result<String> json =  mappingConfigLoader.retrieveVirtualPageIndexMapping();
        Result<String> target = buildVirtualPageMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
        resp.forEach(close);
    }
    
    public void createTermCompletionIndex(@NotNull Group group){
        Result<String> target = queryUtils.indexName(group,IndexNameSuffix.TERM_COMPLETION.value());
        Result<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.forEach(r -> elasticApiUtils.logJson(r));
        
        Result<XContentBuilder> rContentBuilder = createTermCompleteIndexMapping();
        request.forEach(r -> elasticApiUtils.logJson(r));
        
                
        Result<CreateIndexRequest> requestWithContent = 
                rContentBuilder.flatMap(cb -> request.flatMap(r -> addSource(r, cb)));
        requestWithContent.forEach(r -> elasticApiUtils.logJson(r));
        Result<CreateIndexResponse> rResponse = requestWithContent.flatMap(r -> doCreateIndex(r));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                .forEach(e -> LOG.log(Level.INFO, "{0}", e));
    }
    
    public void createPhraseCompletionIndex(@NotNull Group group){
        Result<String> target = queryUtils.indexName(group,IndexNameSuffix.PHRASE_COMPLETION.value());
        Result<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.forEach(r -> elasticApiUtils.logJson(r));
        
        Result<XContentBuilder> rContentBuilder = createPhraseCompleteIndexMapping();
        request.forEach(r -> elasticApiUtils.logJson(r));
        
        Result<CreateIndexRequest> requestWithContent = 
                rContentBuilder.flatMap(cb -> request.flatMap(r -> addSource(r, cb)));
        requestWithContent.forEach(r -> elasticApiUtils.logJson(r));
        Result<CreateIndexResponse> rResponse = requestWithContent.flatMap(r -> doCreateIndex(r));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                .forEach(e -> LOG.log(Level.INFO, "{0}", e));
    }
    
     public void tryCreateUtilIndex(){
        if(!exists(IndexNameSuffix.MUTEX_UTIL.value())){
            LOG.log(Level.INFO, "... CREATING UTIL INDEX ...");
            Result<String> json =  mappingConfigLoader.retrieveUtilIndexMapping();
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
                            .getHighLevelPostClient().indices().create(request, RequestOptions.DEFAULT));
        } catch (Exception ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
    }
      
    private boolean exists(String index){
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return apiClientUtils.getHighLevelPostClient()
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
    
    private Result<XContentBuilder> createTermCompleteIndexMapping(){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {   
                
                builder.startObject("properties");
                {
                    builder.startObject("page_uuid");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("term_completion");
                    {
                        builder.field("type", "completion");
                    }
                    builder.endObject();     
                }
                builder.endObject();
            }
            builder.endObject();
            return Result.success(builder);
        } catch (IOException ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
        
    }
    
    
     private Result<XContentBuilder> createPhraseCompleteIndexMapping(){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {   
                
                builder.startObject("properties");
                {
                    builder.startObject("page_uuid");
                    {
                        builder.field("type", "keyword");
                    }
                    builder.endObject();
                    builder.startObject("phrase_completion");
                    {
                        builder.field("type", "completion");
                    }
                    builder.endObject();     
                }
                builder.endObject();
            }
            builder.endObject();
            return Result.success(builder);
        } catch (IOException ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
        
    }
    
    
    
//    private Result<XContentBuilder> createShingleIndexMapping(){
//         try {
//            XContentBuilder builder = XContentFactory.jsonBuilder();
//            builder.startObject();
//            {   
//                builder.startObject("settings");
//                {
//                    builder.startObject("analysis");
//                    {
//                        builder.startObject("filter");
//                        {
//                            builder.startObject("shingle");
//                            {
//                                builder.field("type", "shingle");
//                                builder.field("min_shingle_size", 2);
//                                builder.field("max_shingle_size", 4);
//                            }
//                            builder.endObject();
//                        }
//                        builder.endObject();
//                    }
//                    builder.endObject();
//                    builder.startObject("analyzer");
//                    {
//                       builder.startObject("mutex_shingle");
//                       {
//                           builder.field("tokenizer", "standard");
//                           builder.array("filter", "shingle");
//                       }
//                       builder.endObject();
//                    }
//                    builder.endObject();
//                }
//                builder.endObject();
//                
//                builder.startObject("settings");
//                {
//                    
//                }
//                builder.endObject();
//            }
//            builder.endObject();
//            return Result.success(builder);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
//            return Result.failure(ex);
//        }
//    }
    
    private final Function<Group,Result<String>> buildMetadataMappingUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()
                + queryUtils.indexName(g,IndexNameSuffix.METADATA.value());
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<Group,Result<String>> buildVirtualPageMappingUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value() 
                + queryUtils.indexName(g,IndexNameSuffix.VIRTUAL_PAGE.value());
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
//    private Result<String> buildCompletionIndex(Group group,String suffix){
//        String target = queryUtils.indexName(group,suffix);
//        LOG.log(Level.INFO, "--> INDEX NAME: {0}", target);
//        return Result.of(target);
//    }
    
//    private Result<String> buildTermCompletionIndex(Group group){
//        String target = queryUtils.indexName(group,IndexNameSuffix.TERM_COMPLETION.value());
//        LOG.log(Level.INFO, "--> INDEX NAME: {0}", target);
//        return Result.of(target);
//    }
//    
//    private Result<String> buildPhraseCompletionIndex(Group group){
//        String target = queryUtils.indexName(group,IndexNameSuffix.TERM_COMPLETION.value());
//        LOG.log(Level.INFO, "--> INDEX NAME: {0}", target);
//        return Result.of(target);
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
