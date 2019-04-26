/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import quantum.functional.api.Result;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.util.QueryUtils;
import quantum.mutex.util.CompletionProperty;
import quantum.mutex.util.ElasticApiUtils;
import quantum.mutex.util.IndexNameSuffix;

/**
 *
 * @author Florent
 */
@Stateless
public class CompletionService {

    private static final Logger LOG = Logger.getLogger(CompletionService.class.getName());
    
    @Inject AnalyzeService analyzeService;
    @Inject ElasticApiUtils elasticApiUtils;
    @Inject QueryUtils queryUtils;
    @Inject ApiClientUtils apiClientUtils;
    
    public void index(FileInfo fileInfo){
//                 fileInfo.get.stream()
//                .map(p -> provideAutoCompleteTerms(p,analyzeService
//                        .analyzeText(p.getContent(),fileInfo.getFileLanguage())))
//                .collect(Collectors.toList());

//          pagesWithCompletionTerms.stream()
//                .forEach(vp -> indexCompletion(fileInfo, vp));
//     
        List<String> analyzedText =
                analyzeService.analyzeText(fileInfo.getRawContent(), fileInfo.getFileLanguage());
        
        analyzedText.forEach(at -> indexTermCompletionData(fileInfo.getGroup(),
                        fileInfo.getInode().getUuid().toString(), at));
        
    }  
    
//     private void indexCompletion(@NotNull FileInfo fileInfo,@NotNull VirtualPage vp){
//        vp.getTermCompletionSuggest().stream()
//                .forEach(t ->indexCompletionData(fileInfo.getGroup(),vp.getUuid() ,t) );
//        
//    }
//     
     private void indexTermCompletionData(Group group,String pageUUID,String input){
        Result<String> target = queryUtils.indexName(group,IndexNameSuffix.TERM_COMPLETION.value());
        Result<IndexRequest> request = target.map(t -> new IndexRequest(t));
//        request.forEach(r -> logJson(r));
        Result<XContentBuilder> rContentBuilder = createCompletionObject(pageUUID,input);
//        request.forEach(r -> logJson(r));
        Result<IndexRequest> requestWithSource = 
                rContentBuilder.flatMap(cb -> request.flatMap(r -> addSource(r, cb)));
//        requestWithSource.forEach(r -> logJson(r));
        Result<IndexResponse> rResponse = requestWithSource.flatMap(r -> indexCompletion(r));
        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
                .forEach(e -> e.printStackTrace());
    }    
     
//    private Result<String> buildCompletionIndex(Group group){
//        String target = queryUtils.getCompletionIndexName(group);
//                
//        LOG.log(Level.INFO, "--> INDEX NAME: {0}", target);
//        return Result.of(target);
//    }
    
    private Result<XContentBuilder> createCompletionObject(String pageUUID,String input){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            
            builder.startObject();
            {
                builder.field("page_uuid", pageUUID);
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
    
    private Result<IndexRequest> addSource(IndexRequest request,
            XContentBuilder xContentBuilder){
        request.source(xContentBuilder);
        return Result.of(request);
    }
    
      private Result<IndexResponse> indexCompletion(IndexRequest request){
        LOG.log(Level.INFO,"---- INDEX COMPLETION ----");
        try {
  
            return Result.success(apiClientUtils
                            .getHighLevelPostClient().index(request, RequestOptions.DEFAULT));
        } catch (Exception ex) {
            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            return Result.failure(ex);
        }
        
    }
      
}
