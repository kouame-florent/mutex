/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import mutex.search.service.AnalyzeService;

/**
 *
 * @author Florent
 */
@Stateless
public class ElApiUtil {

    private static final Logger LOG = Logger.getLogger(ElApiUtil.class.getName());
                
    public void logJson(CreateIndexRequest request){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            builder.prettyPrint();
            request.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "... SEARCH REQUEST JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logJson(CreateIndexResponse response){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            builder.prettyPrint();
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "... CREATE INDEX RESPONSE JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logJson(SearchRequest request){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            builder.prettyPrint();
            request.source().toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "... SEARCH REQUEST JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public void logJson(IndexRequest request){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            builder.prettyPrint();
            request.source().toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "... INDEX REQUEST JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public void logJson(IndexResponse response){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "... INDEX RESPONSE JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logJson(SearchResponse response){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            builder.prettyPrint();
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "... SEARCH RESPONSE JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
     public void logJson(AnalyzeResponse response){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "-->-- ANALYZE REQUEST JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public void logJson(CompletionSuggestionBuilder suggestionBuilder){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            suggestionBuilder.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "-->-- ANALYZE REQUEST JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logJson(SuggestBuilder suggestBuilder){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            suggestBuilder.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "-->-- SUGGEST BUILDER JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public void logJson(SearchSourceBuilder searchSourceBuilder){
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.humanReadable(true);
            searchSourceBuilder.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "-->-- SEARCH SOURCE JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void handle(BulkResponse bulkResponse){
        for (BulkItemResponse bulkItemResponse : bulkResponse) { 
            DocWriteResponse itemResponse = bulkItemResponse.getResponse(); 

            switch (bulkItemResponse.getOpType()) {
                case INDEX:   
                    IndexResponse indexResponse = (IndexResponse) itemResponse;
                    logJson(indexResponse);
                    break;
                case CREATE:
//                    IndexResponse indexResponse = (IndexResponse) itemResponse;
                    break;
                case UPDATE:   
                    UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                    break;
                case DELETE:   
                    DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                }
        }
    }
    
}
