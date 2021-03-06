/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;


import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import io.mutex.user.entity.Group;
import io.mutex.search.valueobject.Metadata;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.index.valueobject.CompletionProperty;
import io.mutex.index.valueobject.IndexNameSuffix;
import io.mutex.search.valueobject.PhraseCompletion;
import javax.validation.constraints.NotBlank;




/**
 *
 * @author Florent
 */
@Stateless
public class DocumentServiceImpl implements DocumentService {

    private static final Logger LOG = Logger.getLogger(DocumentServiceImpl.class.getName());
   
    @Inject IndexNameUtils queryUtils;
    @Inject RestClientUtil apiClientUtils;
    @Inject ElApiLogUtil elApiLogUtils;
      
    @Override
    public void indexMetadata(Metadata metadata,Group group){
        Optional<IndexRequest> rIndexRequest = buildMetadataRequest(metadata, group);
        Optional<IndexResponse> rIndexResponse = rIndexRequest.flatMap(r -> index(r));
        rIndexResponse.ifPresent(r -> elApiLogUtils.logJson(r));

    }
        
    @Override
    public void indexVirtualPage(List<VirtualPage> virtualPages,Group group){
        Optional<BulkRequest> rBulkRequest = buildVirtualPageBulkRequest(virtualPages, group);
        Optional<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> bulkIndex(b));
        rBulkResponse.ifPresent(b -> elApiLogUtils.handle(b));
    }
    
    @Override
    public void indexPhraseCompletion(List<PhraseCompletion> phraseCompletions,Group group){
        Optional<BulkRequest> rBulkRequest = buildPhraseBulkRequest(phraseCompletions, group);
        Optional<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> bulkIndex(b));
      
        rBulkResponse.ifPresent(b -> elApiLogUtils.handle(b));   
    }
    
     private Optional<BulkRequest> buildPhraseBulkRequest(List<PhraseCompletion> phraseCompletions, Group group){
        BulkRequest bulkRequest = new BulkRequest();
        Optional<String> target = queryUtils.getName(group, IndexNameSuffix.PHRASE_COMPLETION.suffix());
        
        phraseCompletions.stream()
                .map(v -> target.flatMap(t -> addSource(v, t)))
                .flatMap(Optional::stream)
                .forEach(i -> addRequest(bulkRequest, i));
        
        return Optional.of(bulkRequest);
    }
    
    @Override
    public void indexCompletionTerm(List<String> terms,Group group,String fileHash,String inodeUUID,
            IndexNameSuffix indexNameSuffix){
        Optional<BulkRequest> rBulkRequest = buildTermBulkRequest(terms, group,fileHash,inodeUUID,
                indexNameSuffix.suffix());
        Optional<BulkResponse> rBulkResponse = rBulkRequest.flatMap(b -> bulkIndex(b));
//        rBulkResponse.forEach(b -> elasticApiUtils.handle(b));
    }
    
    private Optional<BulkRequest> buildTermBulkRequest(List<String> terms, Group group,
            String fileHash,String inodeUUID, String index){
        BulkRequest bulkRequest = new BulkRequest();
        Optional<String> target = queryUtils.getName(group, index);
        
        List<XContentBuilder> contentBuilders = terms.stream()
                .map(t -> createTermCompletion(fileHash, inodeUUID, t))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        
        contentBuilders.stream().map(cb -> target.flatMap(t ->  addSource(cb, t)))
                .flatMap(Optional::stream)
                .forEach(i -> addRequest(bulkRequest, i));
       
        return Optional.of(bulkRequest);
    }
    
    private Optional<IndexRequest> buildMetadataRequest(Metadata metadata,Group group){
        Optional<String> target = queryUtils.getName(group, IndexNameSuffix.METADATA.suffix());
        return target.flatMap(t -> addSource(metadata, t));
   }

    private Optional<IndexRequest> addSource(XContentBuilder xContentBuilder,String target){
        IndexRequest indexRequest = new IndexRequest(target);
        return Optional.of(indexRequest.source(xContentBuilder));
                
    }
       
    private Optional<XContentBuilder> createTermCompletion(@NotBlank String filHash,@NotBlank String inodeUUID,
            @NotBlank String input){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder(); 
            
            builder.startObject();
            {
                builder.field("file_hash", filHash);
                builder.field("inode_uuid", inodeUUID);
                builder.startObject(CompletionProperty.TERM_COMPLETION.value());
                {
                    builder.field("input", input);
                }
                builder.endObject();
            }
            builder.endObject();
            return Optional.ofNullable(builder);
        } catch (IOException ex) {
            Logger.getLogger(IndicesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
        
    }
    
    private Optional<IndexResponse> index(IndexRequest request){
        try {
            return Optional
                    .ofNullable(apiClientUtils.getElClient().index(request, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            Logger.getLogger(DocumentServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
         
    private Optional<BulkResponse> bulkIndex(BulkRequest bulkRequest){
        try {
            return Optional
                    .ofNullable(apiClientUtils.getElClient().bulk(bulkRequest, RequestOptions.DEFAULT));
        } catch (IOException ex) {
            Logger.getLogger(DocumentServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
     
    private void addRequest(BulkRequest bulkRequest,IndexRequest request){
        bulkRequest.add(request);
    }
    
    private Optional<IndexRequest> addSource(Metadata metadata,String target){
        IndexRequest indexRequest = new IndexRequest(target);
        return toMatadataJson(metadata)
                .map(j -> indexRequest.source(j, XContentType.JSON));
                
    }
 
    private Optional<BulkRequest> buildVirtualPageBulkRequest(List<VirtualPage> virtualPages, Group group){
        BulkRequest bulkRequest = new BulkRequest();
        Optional<String> target = queryUtils.getName(group, IndexNameSuffix.VIRTUAL_PAGE.suffix());
        virtualPages.stream().map(v -> target.flatMap(t -> addSource(v, t)))
                .filter(Optional::isPresent).map(Optional::get)
                .forEach(i -> addRequest(bulkRequest, i));
        
        return Optional.of(bulkRequest);
    }
    
       
    private Optional<IndexRequest> addSource(PhraseCompletion phrase,String target){
        IndexRequest indexRequest = new IndexRequest(target);
        return toPhraseCompletionJson(phrase)
                .map(j -> indexRequest.source(j, XContentType.JSON));
    }
    
    private Optional<IndexRequest> addSource(VirtualPage virtualPage,String target){
        IndexRequest indexRequest = new IndexRequest(target);
        return toVirtualPageJson(virtualPage)
                .map(j -> indexRequest.source(j, XContentType.JSON));
    }
   
    private Optional<String> toMatadataJson(Metadata mdto){
        LOG.log(Level.INFO,"--- || ---> META DATE: {0}", 
                mdto.getFileCreated());
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("inode_uuid", mdto.getInodeUUID());
        jsonMap.put("group_uuid", mdto.getGroupUUID());
        jsonMap.put("file_name", mdto.getFileName());
        jsonMap.put("file_size", String.valueOf(mdto.getFileSize()));
        jsonMap.put("file_owner", mdto.getFileOwner());
        jsonMap.put("file_group", mdto.getFileGroup());
        jsonMap.put("file_space", mdto.getFileSpace());
        jsonMap.put("file_created", String.valueOf(mdto.getFileCreated()) );   
        jsonMap.put("content", mdto.getContent());
        jsonMap.put("permissions", mdto.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
        return Optional.of(jsonString);
        
    }
    
    private Optional<String> toVirtualPageJson(VirtualPage page){
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("page_uuid", page.getUuid());
        jsonMap.put("page_hash", page.getPageHash());
        jsonMap.put("inode_uuid", page.getInodeUUID());
        jsonMap.put("group_uuid", page.getGroupUUID());
        jsonMap.put("file_name", page.getFileName());
        jsonMap.put("content", page.getContent());
        jsonMap.put("total_page_count", String.valueOf(page.getTotalPageCount()));
        jsonMap.put("page_index", String.valueOf(page.getPageIndex()));
        jsonMap.put("permissions", page.getPermissions());
        
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
        return Optional.of(jsonString);
    }
    
    private Optional<String> toPhraseCompletionJson(PhraseCompletion phrase){
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("inode_uuid", phrase.getInodeUuid());
        jsonMap.put("content", phrase.getContent());
                
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonMap);
        return Optional.of(jsonString);
    }
 
}
