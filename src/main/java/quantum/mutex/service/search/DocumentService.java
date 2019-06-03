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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
import quantum.functional.api.Result;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.dto.Metadata;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.util.CompletionProperty;
import quantum.mutex.util.Constants;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.IndexNameSuffix;



/**
 *
 * @author Florent
 */
@Stateless
public class DocumentService {

    private static final Logger LOG = Logger.getLogger(DocumentService.class.getName());
   
    @Inject QueryUtils queryUtils;
    @Inject RestClientUtil apiClientUtils;
    @Inject ElApiUtil elApiUtils;
      
    public void indexMetadata(Metadata metadata,Group group){
        Result<IndexRequest> rIndexRequest = buildMetadataRequest(metadata, group);
        Result<IndexResponse> rIndexResponse = rIndexRequest.flatMap(r -> index(r));
        rIndexResponse.forEachOrException(r -> elApiUtils.logJson(r))
                .forEach(e -> e.printStackTrace());
    }
        
    public void indexVirtualPage(List<VirtualPage> virtualPages,Group group){
        Result<BulkRequest> rBulkRequest = buildVirtualPageBulkRequest(virtualPages, group);
        Result<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> bulkIndex(b));
        rBulkResponse.forEach(b -> elApiUtils.handle(b));
    }
    
    public void indexCompletion(List<String> terms,Group group,String fileHash,IndexNameSuffix indexNameSuffix){
        Result<BulkRequest> rBulkRequest = buildBulkRequest(terms, group,fileHash,
                indexNameSuffix.value());
        Result<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> bulkIndex(b));
//        rBulkResponse.forEach(b -> elasticApiUtils.handle(b));
    }
    
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
    
    private Result<IndexRequest> buildMetadataRequest(Metadata metadata,Group group){
        Result<String> target = queryUtils.indexName(group, IndexNameSuffix.METADATA.value());
        return target.flatMap(t -> addSource(metadata, t));
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
    
    private Result<IndexResponse> index(IndexRequest request){
        try {
            return Result
                    .success(apiClientUtils.getElClient().index(request, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            Logger.getLogger(DocumentService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
    }
         
    private Result<BulkResponse> bulkIndex(BulkRequest bulkRequest){
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
   
    private Result<String> toMatadataJson(Metadata mdto){
        LOG.log(Level.INFO,"--- || ---> META DATE: {0}", 
                mdto.getFileCreated());
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("inode_uuid", mdto.getInodeUUID());
        jsonMap.put("file_name", mdto.getFileName());
        jsonMap.put("file_size", String.valueOf(mdto.getFileSize()));
        jsonMap.put("file_owner", mdto.getFileOwner());
        jsonMap.put("file_group", mdto.getFileGroup());
        jsonMap.put("file_tenant", mdto.getFileTenant());
        jsonMap.put("file_created", String.valueOf(mdto.getFileCreated()) );   
        jsonMap.put("content", mdto.getContent());
        jsonMap.put("permissions", mdto.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
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
        return Result.of(jsonString);
    }
 
}
