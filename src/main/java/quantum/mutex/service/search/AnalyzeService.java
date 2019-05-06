/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.search;

import quantum.mutex.util.RestClientUtil;
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
import quantum.functional.api.Result;
import quantum.mutex.util.Constants;
import quantum.mutex.util.ElApiUtil;
import quantum.mutex.util.IndexNameSuffix;
import quantum.mutex.util.MutexUtilAnalyzer;

/**
 *
 * @author Florent
 */
@Stateless
public class AnalyzeService{

    private static final Logger LOG = Logger.getLogger(AnalyzeService.class.getName());
    
    @Inject RestClientUtil restClientUtil;
    @Inject ElApiUtil apiUtil;
    
    public List<String> analyzeForTerms(String text){
//        LOG.log(Level.INFO, "--> RAW TEXT {0}", text);
        Result<AnalyzeRequest> rRequest = restClientUtil.getAnalyzeRequest()
                .flatMap(ar -> initTermAnalyzer(ar,text));
        
        //rRequest.forEach(apiUtil::logJson);
        
        Result<AnalyzeResponse> rResponse = rRequest
                .flatMap(r -> sendRequest(r, restClientUtil.getElClient()));
        rResponse.forEachOrException(apiUtil::logJson).forEach(e -> e.printStackTrace());
      
        List<String> terms = rResponse.map(r -> getToken(r)).getOrElse(() -> Collections.EMPTY_LIST);
        terms.forEach(t -> LOG.log(Level.INFO, "-->COMPLETION TERM: {0}", t));
        
        return filterTerms(terms);
        
//        return Collections.EMPTY_LIST;

    }
    
  
    
    
    public List<String> analyzeForPhrase(String text,IndexNameSuffix suffix){
        LOG.log(Level.INFO, "... ANALYZE PHRASE  ... ");
        Result<AnalyzeRequest> rRequest = restClientUtil.getAnalyzeRequest(suffix.value())
                .flatMap(ar -> initPhraseAnalyzer(ar,text));
        
        rRequest.forEach(apiUtil::logJson);
        
        Result<AnalyzeResponse> rResponse = rRequest
                .flatMap(r -> sendRequest(r, restClientUtil.getElClient()));
        
        rResponse.forEach(apiUtil::logJson);
      
        List<String> terms = rResponse.map(r -> getToken(r)).getOrElse(() -> Collections.EMPTY_LIST);
        terms.forEach(t -> LOG.log(Level.INFO, "--> COMPLETION PHRASE: {0}", t));
        
        return filterTerms(terms);

    }
    
    private Result<AnalyzeRequest> initTermAnalyzer(AnalyzeRequest request,String text){
       request.text(text);
       request.analyzer("standard");
       return Result.of(request);
    }
    
    private Result<AnalyzeRequest> initPhraseAnalyzer(AnalyzeRequest request,String text){
       request.text(text);
       request.analyzer(MutexUtilAnalyzer.SHINGLE.value());
       return Result.of(request);
    }
    
    private List<String> filterTerms(List<String> terms){
        return terms.stream()
                .filter(t -> t.length() >= Constants.AUTOCOMPLETE_TOKEN_MAX_SIZE)
                .distinct().collect(Collectors.toList());
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
    
}
