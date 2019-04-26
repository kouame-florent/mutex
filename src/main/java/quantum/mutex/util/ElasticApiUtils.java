/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import quantum.mutex.service.search.AnalyzeService;

/**
 *
 * @author Florent
 */
@Stateless
public class ElasticApiUtils {

    private static final Logger LOG = Logger.getLogger(ElasticApiUtils.class.getName());
                
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
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            LOG.log(Level.INFO, "... SEARCH RESPONSE JSON: {0}",Strings.toString(builder));
             
        } catch (IOException ex) {
            Logger.getLogger(AnalyzeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
