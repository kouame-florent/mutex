/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.QueryUtils;
import quantum.mutex.util.RestClientUtil;


/**
 *
 * @author Florent
 */
@Stateless
public class CompletionService {

    private static final Logger LOG = Logger.getLogger(CompletionService.class.getName());
    
    @Inject AnalyzeService analyzeService;
    @Inject ElApiUtil elasticApiUtils;
    @Inject QueryUtils queryUtils;
    @Inject RestClientUtil apiClientUtils;
    
//    public void index(FileInfo fileInfo){
//
//        List<String> analyzedText =
//                analyzeService.analyzeText(fileInfo.getRawContent(), fileInfo.getFileLanguage());
//        
//        List<String> filteredTerms = filterTerms(analyzedText);
//        
////        analyzedText.forEach(at -> indexTermCompletionData(fileInfo.getGroup(),
////                        fileInfo.getInode().getUuid().toString(), at));
//        
//    }  
    
//    private List<String> filterTerms(List<String> terms){
//        return terms.stream()
//                .filter(t -> t.length() >= Constants.AUTOCOMPLETE_TOKEN_MAX_SIZE)
//                .distinct().collect(Collectors.toList());
//    }
   
//     private void indexTermCompletionData(Group group,String pageUUID,String input){
//        Result<String> target = queryUtils.indexName(group,IndexNameSuffix.TERM_COMPLETION.value());
//        Result<IndexRequest> request = target.map(t -> new IndexRequest(t));
////        request.forEach(r -> logJson(r));
//        Result<XContentBuilder> rContentBuilder = createCompletionObject(pageUUID,input);
////        request.forEach(r -> logJson(r));
//        Result<IndexRequest> requestWithSource = 
//                rContentBuilder.flatMap(cb -> request.flatMap(r -> addSource(r, cb)));
////        requestWithSource.forEach(r -> logJson(r));
//        Result<IndexResponse> rResponse = requestWithSource.flatMap(r -> indexCompletion(r));
//        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
//                .forEach(e -> e.printStackTrace());
//    }    
     
//    private Result<XContentBuilder> createCompletionObject(String pageUUID,String input){
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
//    
//    private Result<IndexRequest> addSource(IndexRequest request,
//            XContentBuilder xContentBuilder){
//        request.source(xContentBuilder);
//        return Result.of(request);
//    }
//    
//      private Result<IndexResponse> indexCompletion(IndexRequest request){
//        LOG.log(Level.INFO,"---- INDEX COMPLETION ----");
//        try {
//  
//            return Result.success(apiClientUtils
//                            .getHighLevelClient().index(request, RequestOptions.DEFAULT));
//        } catch (Exception ex) {
//            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
//            return Result.failure(ex);
//        }
//        
//    }
      
}
