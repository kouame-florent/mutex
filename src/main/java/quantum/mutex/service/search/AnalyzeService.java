/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import quantum.functional.api.Result;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Stateless
public class AnalyzeService extends SearchBaseService{

    private static final Logger LOG = Logger.getLogger(AnalyzeService.class.getName());
    
    @Inject ApiClientUtils apiClientUtils;
    
    public List<String> analyzeText(String text,String lang){
            
        Result<AnalyzeRequest> rRequest = apiClientUtils.getAnalyzeRequest()
                .flatMap(ar -> initTermAnalyzer(ar,text,lang));
        
        rRequest.forEach(this::logJson);
        
        Result<AnalyzeResponse> rResponse = rRequest
                .flatMap(r -> sendRequest(r, apiClientUtils.getHighLevelPostClient()));
      
        return rResponse.map(r -> getToken(r)).getOrElse(() -> Collections.EMPTY_LIST);

    }
    
    public void logJson(AnalyzeRequest request){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            request.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "-->-- ANALYZE REQUEST JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Result<AnalyzeResponse> sendRequest(AnalyzeRequest request,RestHighLevelClient client){
        try {
            AnalyzeResponse response = client.indices().analyze(request, RequestOptions.DEFAULT);
            return Result.success(response);
        } catch (IOException ex) {
            ex.printStackTrace();
            return Result.failure(ex);
        }
    }
 
    private List<String> getToken(AnalyzeResponse response){
       LOG.log(Level.INFO, "--|> .... GETTING TOKENS .....");  
       List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
       LOG.log(Level.INFO, "--|> TOKEN SIZE: {0}", tokens.size());  
       return tokens.stream().map(t -> t.getTerm())
               .filter(t -> t.length() >= Constants.AUTOCOMPLETE_TOKEN_MAX_SIZE)
               .collect(Collectors.toList());
    }
    
    private Result<AnalyzeRequest> initTermAnalyzer(AnalyzeRequest request,String text,String lang){
       request.text(text);
       request.analyzer("standard");
//        request.addCharFilter("html_strip");                
//        request.tokenizer("standard");                      
//        request.addTokenFilter("lowercase");   
        return Result.of(request);
    }
    
}
