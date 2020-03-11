/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;

/**
 *
 * @author florent
 */
public interface ElApiLogUtil {

    void handle(BulkResponse bulkResponse);

    void logJson(CreateIndexRequest request);

    void logJson(CreateIndexResponse response);

    void logJson(SearchRequest request);

    void logJson(IndexRequest request);

    void logJson(IndexResponse response);

    void logJson(SearchResponse response);

    void logJson(AnalyzeRequest request);

    void logJson(AnalyzeResponse response);

    void logJson(CompletionSuggestionBuilder suggestionBuilder);

    void logJson(SuggestBuilder suggestBuilder);

    void logJson(SearchSourceBuilder searchSourceBuilder);
    
}
