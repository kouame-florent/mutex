/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;


import quantum.mutex.util.RestClientUtil;
import quantum.mutex.util.QueryUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import quantum.functional.api.Effect;
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.util.CompletionProperty;
import quantum.mutex.util.Constants;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.ServiceEndPoint;


/**
 *
 * @author Florent
 */
@Stateless
public class DocumentService {

    private static final Logger LOG = Logger.getLogger(DocumentService.class.getName());
   
    @Inject QueryUtils queryUtils;
    @Inject RestClientUtil apiClientUtils;
    @Inject ElApiUtil elasticApiUtils;
      
    public void indexMetadata(List<Metadata> metadatas,Group group){
        Result<BulkRequest> rBulkRequest = buildMetadataBulkRequest(metadatas, group);
        Result<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> sendBulkIndex(b));
        rBulkResponse.forEach(b -> elasticApiUtils.handle(b));
    }
        
    public void indexVirtualPage(List<VirtualPage> virtualPages,Group group){
        Result<BulkRequest> rBulkRequest = buildVirtualPageBulkRequest(virtualPages, group);
        Result<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> sendBulkIndex(b));
        rBulkResponse.forEach(b -> elasticApiUtils.handle(b));
    }
    
    public void indexCompletion(List<String> terms,Group group,String fileHash,IndexNameSuffix indexNameSuffix){
        Result<BulkRequest> rBulkRequest = buildBulkRequest(terms, group,fileHash,
                indexNameSuffix.value());
        Result<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> sendBulkIndex(b));
//        rBulkResponse.forEach(b -> elasticApiUtils.handle(b));
    }
    
//    public void indexPhraseCompletion(List<String> terms,Group group,String fileHash){
//        Result<BulkRequest> rBulkRequest = buildBulkRequest(terms, group,fileHash,
//                IndexNameSuffix.PHRASE_COMPLETION.value());
//        Result<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> sendBulkIndex(b));
//        rBulkResponse.forEach(b -> elasticApiUtils.handle(b));
//    }
    
//    private Result<BulkRequest> buildTermBulkRequest(List<String> terms, Group group,String fileHash){
//        BulkRequest bulkRequest = new BulkRequest();
//        Result<String> target = queryUtils.indexName(group, IndexNameSuffix.TERM_COMPLETION.value());
//        
//        List<XContentBuilder> contentBuilders = terms.stream()
//                .map(t -> createCompletion(fileHash, t))
//                .filter(Result::isSuccess)
//                .map(Result::successValue).collect(Collectors.toList());
//        
//        contentBuilders.stream().map(cb -> target.flatMap(t ->  addSource(cb, t)))
//                .filter(Result::isSuccess).map(Result::successValue)
//                .forEach(i -> addRequest(bulkRequest, i));
//       
//        return Result.of(bulkRequest);
//    }
    
    private Result<BulkRequest> buildBulkRequest(List<String> phrases, Group group,
            String fileHash,String index){
        BulkRequest bulkRequest = new BulkRequest();
        Result<String> target = queryUtils.indexName(group, index);
        
        List<XContentBuilder> contentBuilders = phrases.stream()
                .map(t -> createCompletion(fileHash, t))
                .filter(Result::isSuccess)
                .map(Result::successValue).collect(Collectors.toList());
        
        contentBuilders.stream().map(cb -> target.flatMap(t ->  addSource(cb, t)))
                .filter(Result::isSuccess).map(Result::successValue)
                .forEach(i -> addRequest(bulkRequest, i));
       
        return Result.of(bulkRequest);
    }
    
    private Result<BulkRequest> buildMetadataBulkRequest(List<Metadata> metadatas,Group group){
        BulkRequest bulkRequest = new BulkRequest();
        Result<String> target = queryUtils.indexName(group, IndexNameSuffix.METADATA.value());
        metadatas.stream().map(m -> target.flatMap(t -> addSource(m, t)))
                .filter(Result::isSuccess).map(Result::successValue)
                .forEach(i -> addRequest(bulkRequest, i));
        
        return Result.of(bulkRequest);
    }
    
    private Result<IndexRequest> addSource(XContentBuilder xContentBuilder,String target){
        IndexRequest indexRequest = new IndexRequest(target);
        return Result.of(indexRequest.source(xContentBuilder));
                
    }
       
    private Result<XContentBuilder> createCompletion(String filHash,String input){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            
            builder.startObject();
            {
                builder.field("file_hash", filHash);
                builder.startObject(CompletionProperty.TERM_COMPLETION.value());
                {
                    builder.field("input", input);
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
    
     
    private Result<BulkResponse> sendBulkIndex(BulkRequest bulkRequest){
        try {
            return Result
                    .success(apiClientUtils.getElClient().bulk(bulkRequest, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            Logger.getLogger(DocumentService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
    }
     
    private void addRequest(BulkRequest bulkRequest,IndexRequest request){
        bulkRequest.add(request);
    }
    
    private Result<IndexRequest> addSource(Metadata metadata,String target){
        IndexRequest indexRequest = new IndexRequest(target);
        return toMatadataJson(metadata)
                .map(j -> indexRequest.source(j, XContentType.JSON));
                
    }
    
    
    
//    private Result<XContentBuilder> createTermCompletion(String pageUUID,String input){
//        try {
//            XContentBuilder builder = XContentFactory.jsonBuilder();
//            
//            builder.startObject();
//            {
//                builder.field("page_uuid", pageUUID);
//                builder.startObject(CompletionProperty.TERM_COMPLETION.value());
//                {
//                    builder.field("input", input);
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
  
    private Result<BulkRequest> buildVirtualPageBulkRequest(List<VirtualPage> virtualPages, Group group){
        BulkRequest bulkRequest = new BulkRequest();
        Result<String> target = queryUtils.indexName(group, IndexNameSuffix.VIRTUAL_PAGE.value());
        virtualPages.stream().map(v -> target.flatMap(t -> addSource(v, t)))
                .filter(Result::isSuccess).map(Result::successValue)
                .forEach(i -> addRequest(bulkRequest, i));
        
        return Result.of(bulkRequest);
    }
    
    private Result<IndexRequest> addSource(VirtualPage virtualPage,String target){
        IndexRequest indexRequest = new IndexRequest(target);
        return toVirtualPageJson(virtualPage)
                .map(j -> indexRequest.source(j, XContentType.JSON));
    }
   
    
    public void logJson(IndexResponse response){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "-->-- CREATE INDEX RESPONSE JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    private MultivaluedMap<String,Object> headers(){
//        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
//        headers.add("Accept", "application/json");
//        return headers;
//    }
 
    private Result<String> toMatadataJson(Metadata mdto){
        
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("uuid", mdto.getUuid());
        jsonMap.put("inode_uuid", mdto.getInodeUUID());
        jsonMap.put("file_name", mdto.getFileName());
        jsonMap.put("file_size", String.valueOf(mdto.getFileSize()));
        jsonMap.put("file_owner", mdto.getFileOwner());
        jsonMap.put("file_group", mdto.getFileGroup());
        jsonMap.put("file_tenant", mdto.getFileTenant());
        jsonMap.put("file_created", mdto.getFileCreated()
                .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)));
        jsonMap.put("attribute_name", mdto.getAttributeName());
        jsonMap.put("attribute_value", mdto.getAttributeValue());
        jsonMap.put("permissions", mdto.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
//        LOG.log(Level.INFO, "--> META JSON: {0}", jsonString);
        return Result.of(jsonString);
        
    }
    
    private Result<String> toVirtualPageJson(VirtualPage vpdto){
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("page_uuid", vpdto.getUuid());
        jsonMap.put("page_hash", vpdto.getPageHash());
        jsonMap.put("inode_uuid", vpdto.getInodeUUID());
        jsonMap.put("file_name", vpdto.getFileName());
        jsonMap.put("content", vpdto.getContent());
        jsonMap.put("total_page_count", String.valueOf(vpdto.getTotalPageCount()));
        jsonMap.put("page_index", String.valueOf(vpdto.getPageIndex()));
        jsonMap.put("permissions", vpdto.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
//        LOG.log(Level.INFO, "--> VIRTUAL PAGE JSON: {0}", jsonString);
        return Result.of(jsonString);
    }
 

}
