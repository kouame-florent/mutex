/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import quantum.mutex.domain.dto.FileInfo;
import quantum.mutex.service.search.AnalyzeService;

/**
 *
 * @author Florent
 */
@Stateless
public class CompletionService {

    private static final Logger LOG = Logger.getLogger(CompletionService.class.getName());
    
    @Inject AnalyzeService analyzeService;
    
    public void index(FileInfo fileInfo){
//                 fileInfo.get.stream()
//                .map(p -> provideAutoCompleteTerms(p,analyzeService
//                        .analyzeText(p.getContent(),fileInfo.getFileLanguage())))
//                .collect(Collectors.toList());

//          pagesWithCompletionTerms.stream()
//                .forEach(vp -> indexCompletion(fileInfo, vp));
//     
    
    }  

}
