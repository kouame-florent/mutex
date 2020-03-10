/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.index.valueobject.IndexNameSuffix;
import java.util.List;

/**
 *
 * @author florent
 */
public interface AnalyzeService {

    List<String> analyzeForPhrase(String text, IndexNameSuffix suffix);

    List<String> analyzeForTerms(String text);

    List<String> analyzeForTerms(List<String> texts, String lang);
    
}
