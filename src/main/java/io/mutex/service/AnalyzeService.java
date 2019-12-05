/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.service;

import mutex.util.RestClientUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import mutex.util.Constants;
import mutex.util.ElApiUtil;
import mutex.util.IndexNameSuffix;
import mutex.util.MutexUtilAnalyzer;
import mutex.util.TextService;


/**
 *
 * @author Florent
 */
@Stateless
public class AnalyzeService{

    private static final Logger LOG = Logger.getLogger(AnalyzeService.class.getName());
    
    @Inject RestClientUtil restClientUtil;
    @Inject ElApiUtil apiUtil;
    @Inject TextService textService;
    
    public List<String> analyzeForTerms(String text){
//        LOG.log(Level.INFO, "--> RAW TEXT {0}", text);
        Optional<AnalyzeRequest> rRequest = restClientUtil.getAnalyzeRequest()
                .flatMap(ar -> initTermAnalyzer(ar,text));
        
        //rRequest.forEach(apiUtil::logJson);
        
        Optional<AnalyzeResponse> rResponse = rRequest
                .flatMap(r -> sendRequest(r, restClientUtil.getElClient()));
        rResponse.ifPresent(apiUtil::logJson);
      
        List<String> terms = rResponse.map(r -> getToken(r)).orElseGet(() -> Collections.EMPTY_LIST);
        terms.forEach(t -> LOG.log(Level.INFO, "-->COMPLETION TERM: {0}", t));
        
        return filterTerms(terms);
        
//        return Collections.EMPTY_LIST;

    }
    
     public List<String> analyzeForTerms(List<String> texts,String lang){
        LOG.log(Level.INFO, "--> CHUNK TEXT SIZE {0}", texts.size());
        String wholeText = textService.toText(texts).orElseGet(() -> "");
        Optional<AnalyzeRequest> rRequest = restClientUtil
                .getAnalyzeRequest(IndexNameSuffix.MUTEX_UTIL.value())
                .flatMap(ar -> initTermAnalyzer(ar,wholeText,lang));
//        
//        rRequest.forEach(apiUtil::logJson);
//        
        Optional<AnalyzeResponse> rResponse = rRequest
                .flatMap(r -> sendRequest(r, restClientUtil.getElClient()));
//      rResponse.forEachOrException(apiUtil::logJson).forEach(e -> e.printStackTrace());
//      
        List<String> terms = rResponse.map(r -> getToken(r)).orElseGet(() -> Collections.EMPTY_LIST);
        LOG.log(Level.INFO, "--> TERMS LIST SIZE: {0}", terms.size());
//        terms.forEach(t -> LOG.log(Level.INFO, "-->COMPLETION TERM: {0}", t));
//        
        return terms;
//        
//        return Collections.EMPTY_LIST;

    }
    
    
    public List<String> analyzeForPhrase(String text,IndexNameSuffix suffix){
        LOG.log(Level.INFO, "... ANALYZE PHRASE  ... ");
        Optional<AnalyzeRequest> rRequest = restClientUtil.getAnalyzeRequest(suffix.value())
                .flatMap(ar -> initPhraseAnalyzer(ar,text));
        
        rRequest.ifPresent(apiUtil::logJson);
        
        Optional<AnalyzeResponse> rResponse = rRequest
                .flatMap(r -> sendRequest(r, restClientUtil.getElClient()));
        
        rResponse.ifPresent(apiUtil::logJson);
      
        List<String> terms = rResponse.map(r -> getToken(r)).orElseGet(() -> Collections.EMPTY_LIST);
        terms.forEach(t -> LOG.log(Level.INFO, "--> COMPLETION PHRASE: {0}", t));
        
        return filterTerms(terms);

    }
    
    private Optional<AnalyzeRequest> initTermAnalyzer(AnalyzeRequest request,String text){
       request.text(text);
       request.analyzer("standard");
       return Optional.of(request);
    }
    
    private Optional<AnalyzeRequest> initTermAnalyzer(AnalyzeRequest request,String text,String lang){
       request.text(text);
       if(lang.startsWith("fr")){
           request.analyzer(MutexUtilAnalyzer.COMPLETION_FRENCH.value());
       }
       if(lang.startsWith("en")){
           request.analyzer(MutexUtilAnalyzer.COMPLETION_ENGLISH.value());
       }
       return Optional.of(request);
    }
    
    private Optional<AnalyzeRequest> initPhraseAnalyzer(AnalyzeRequest request,String text){
       request.text(text);
       request.analyzer(MutexUtilAnalyzer.SHINGLE.value());
       return Optional.of(request);
    }
    
    private List<String> filterTerms(List<String> terms){
        return terms.stream()
                .filter(t -> t.length() >= Constants.AUTOCOMPLETE_TOKEN_MAX_SIZE)
                .distinct().collect(Collectors.toList());
    }
    
    private Optional<AnalyzeResponse> sendRequest(AnalyzeRequest request,RestHighLevelClient client){
        try {
            AnalyzeResponse response = client.indices().analyze(request, RequestOptions.DEFAULT);
            return Optional.ofNullable(response);
        } catch (IOException ex) {
            ex.printStackTrace();
            return Optional.empty();
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
