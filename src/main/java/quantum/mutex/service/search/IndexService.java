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
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.config.ElasticMappingConfigLoader;
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
    @Inject QueryUtils elasticApiUtils;
    

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
        Result<String> target = bulidCompletionIndex(group);
        Result<CreateIndexRequest> request = target.map(t -> new CreateIndexRequest(t));
        request.forEach(r -> logJson(r));
        Result<XContentBuilder> rContentBuilder = createCompleteIndexMapping();
        request.forEach(r -> logJson(r));
        Result<CreateIndexRequest> requestWithContent = 
                rContentBuilder.flatMap(cb -> request.flatMap(r -> addContenBuilder(r, cb)));
        requestWithContent.forEach(r -> logJson(r));
        Result<CreateIndexResponse> rResponse = requestWithContent.flatMap(r -> doCreateCompletionIndex(r));
        rResponse.forEachOrException(r -> logJson(r)).forEach(e -> e.printStackTrace());
    }
    
    public void logJson(CreateIndexRequest request){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            request.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "-->-- CREATE INDEX REQUEST JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logJson(CreateIndexResponse response){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "-->-- CREATE INDEX RESPONSE JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Result<CreateIndexResponse>  doCreateCompletionIndex(CreateIndexRequest request){
        LOG.log(Level.INFO,"---- CREATING INDEX ----");
        try {
            return Result.success(apiClientUtils
                            .getRestHighLevelClient().indices().create(request, RequestOptions.DEFAULT));
        } catch (Exception ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
        
    }
    
    private Result<CreateIndexRequest> addContenBuilder(CreateIndexRequest request,
            XContentBuilder xContentBuilder){
        request.mapping(IndexName.COMPLETION.value(), xContentBuilder);
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
                        builder.field("type", "text");
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
    
    private final Function<Group,Result<String>> buildMetadataMappingUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value()
                + elasticApiUtils.getMetadataIndexName(g);
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private final Function<Group,Result<String>> buildVirtualPageMappingUri = g -> {
        String target = ServiceEndPoint.ELASTIC_BASE_URI.value() 
                + elasticApiUtils.getVirtualPageIndexName(g);
        LOG.log(Level.INFO, "--> TARGET: {0}", target);
        return Result.of(target);
    };
    
    private Result<String> bulidCompletionIndex(Group group){
        String target = elasticApiUtils.getCompletionIndexName(group);
        LOG.log(Level.INFO, "--> INDEX NAME: {0}", target);
        return Result.of(target);
    }
    
    private final Effect<Response> close = r -> {
        if(r != null) r.close();
    };
    
    
   
}
