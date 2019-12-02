/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.search.service;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import mutex.util.ElApiUtil;
import mutex.util.QueryUtils;
import mutex.util.RestClientUtil;


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
//        Optional<String> target = queryUtils.indexName(group,IndexNameSuffix.TERM_COMPLETION.value());
//        Optional<IndexRequest> request = target.map(t -> new IndexRequest(t));
////        request.forEach(r -> logJson(r));
//        Optional<XContentBuilder> rContentBuilder = createCompletionObject(pageUUID,input);
////        request.forEach(r -> logJson(r));
//        Optional<IndexRequest> requestWithSource = 
//                rContentBuilder.flatMap(cb -> request.flatMap(r -> addSource(r, cb)));
////        requestWithSource.forEach(r -> logJson(r));
//        Optional<IndexResponse> rResponse = requestWithSource.flatMap(r -> indexCompletion(r));
//        rResponse.forEachOrException(r -> elasticApiUtils.logJson(r))
//                .forEach(e -> e.printStackTrace());
//    }    
     
//    private Optional<XContentBuilder> createCompletionObject(String pageUUID,String input){
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
//            return Optional.success(builder);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
//            return Optional.failure(ex);
//        }
//        
//    }
//    
//    private Optional<IndexRequest> addSource(IndexRequest request,
//            XContentBuilder xContentBuilder){
//        request.source(xContentBuilder);
//        return Optional.of(request);
//    }
//    
//      private Optional<IndexResponse> indexCompletion(IndexRequest request){
//        LOG.log(Level.INFO,"---- INDEX COMPLETION ----");
//        try {
//  
//            return Optional.success(apiClientUtils
//                            .getHighLevelClient().index(request, RequestOptions.DEFAULT));
//        } catch (Exception ex) {
//            Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
//            return Optional.failure(ex);
//        }
//        
//    }
      
}
