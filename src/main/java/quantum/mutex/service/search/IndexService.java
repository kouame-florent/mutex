/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.config.ElasticMappingConfigLoader;
import quantum.mutex.util.ElasticApiUtils;
import quantum.mutex.util.IndexName;
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
        
        Result<String> json =  mappingConfigLoader.retrieveMetadataMapping();
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
        Result<String> json =  mappingConfigLoader.retrieveVirtualPageMapping();
        Result<String> target = buildVirtualPageMappingUri.apply(group);
        Result<Response> resp = target
                .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));
        
        resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
        resp.forEach(close);
    }
    
    public void createCompletionIndex(@NotNull Group group){
        Result<String> target = buildCompletionIndex(group);
        Result<IndexRequest> request = target.map(t -> new IndexRequest(t));
        request.forEach(r -> elasticApiUtils.logJson(r));
        Result<XContentBuilder> rContentBuilder = createCompleteIndexMapping();
        request.forEach(r -> elasticApiUtils.logJson(r));
        Result<IndexRequest> requestWithContent = 
                rContentBuilder.flatMap(cb -> request.flatMap(r -> addSource(r, cb)));
        requestWithContent.forEach(r -> elasticApiUtils.logJson(r));
        Result<IndexResponse> rResponse = requestWithContent.flatMap(r -> doCreateCompletionIndex(r));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r)).forEach(e -> e.printStackTrace());
    }
 
    private Result<IndexResponse>  doCreateCompletionIndex(IndexRequest request){
        LOG.log(Level.INFO,"---- CREATING INDEX ----");
        try {
            return Result.success(apiClientUtils
                            .getHighLevelPostClient().index(request, RequestOptions.DEFAULT));
        } catch (Exception ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
    }
    
    public void tryCreateUtilIndex(){
        if(!exists(IndexName.MUTEX_UTIL.value())){
            Result<String> json =  mappingConfigLoader.retrieveUtilMapping();
            Result<String> target = buildUtilIndex();
            Result<Response> resp = target
                    .flatMap(t -> json.flatMap(j -> apiClientUtils.put(t, Entity.json(j),headers())));

            resp.forEach(r -> LOG.log(Level.INFO, "--> RESPONSE FROM EL: {0}", r.readEntity(String.class)));
            resp.forEach(close);
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
    
    private Result<IndexRequest> addSource(IndexRequest request,
            XContentBuilder xContentBuilder){
        request.source(xContentBuilder);
//        request.mapping(IndexName.COMPLETION.value(), xContentBuilder);
        return Result.of(request);
    }
    
    private Result<XContentBuilder> createCompleteIndexMapping(){
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
                + queryUtils.getMetadataIndexName(g);
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<Group,Result<String>> buildVirtualPageMappingUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value() 
                + queryUtils.getVirtualPageIndexName(g);
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private Result<String> buildCompletionIndex(Group group){
        String target = queryUtils.getCompletionIndexName(group);
        LOG.log(Level.INFO, "--> INDEX NAME: {0}", target);
        return Result.of(target);
    }
    
    private Result<String> buildUtilIndex(){
        String target = IndexName.MUTEX_UTIL.value();
        LOG.log(Level.INFO, "--> INDEX NAME: {0}",target);
        return Result.of(target);
    }
    
    private final Effect<Response> close = r -> {
        if(r != null) r.close();
    };
    
}
