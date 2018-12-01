/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.domain.entity.MutexFile;
import quantum.mutex.domain.dao.MutexFileDAO;
import quantum.mutex.domain.dto.Fragment;
import quantum.mutex.service.PermissionFilterService;
import quantum.mutex.service.api.ElasticQueryUtils;
import quantum.mutex.service.api.ElasticResponseHandler;
import quantum.mutex.service.api.ElasticSearchService;
import quantum.mutex.util.Constants;

/**
 *
 * @author Florent
 */
@Named(value = "searchBacking")
@ViewScoped
public class SearchBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(SearchBacking.class.getName());
     
    @Inject ElasticSearchService searchService;
    @Inject PermissionFilterService permissionFilterService;
    @Inject ElasticQueryUtils elasticApiUtils;
    @Inject ElasticResponseHandler responseHandler;
    @Inject MutexFileDAO mutexFileDAO;
    
    private String searchText;
    private Set<Fragment> fragments = new LinkedHashSet<>();
    
    @PostConstruct
    public void init(){
        
    }
    
    public void search(){
      processSearchStack();
   }
   
   private final Function<String,Set<Fragment>> matchQuery = text -> {
       return getUserPrimaryGroup()
                .flatMap(g -> searchService.searchForMatch(g, text))
                .flatMap(j -> responseHandler.marshall(j))
                .map(jo -> responseHandler.getFragments(jo))
                .getOrElse(() -> Collections.EMPTY_SET);
   };
   
   private final Function<String,Set<Fragment>> matchPhraseQuery = text -> {
       return getUserPrimaryGroup()
                .flatMap(g -> searchService.searchForMatchPhrase(g, text))
                .flatMap(j -> responseHandler.marshall(j))
                .map(jo -> responseHandler.getFragments(jo))
                .getOrElse(() -> Collections.EMPTY_SET);
   };
   

    public void processSearchStack(){
        fragments.clear();
        
        matchPhraseQuery.apply(searchText)
                .forEach(this::addToResult);
        
        if(fragments.size() < Constants.SEARCH_RESULT_THRESHOLD){
            matchQuery.apply(searchText)
                .forEach(this::addToResult);
        }
    }
    
    private void addToResult(Fragment fragment){
        if(!hasReachThreshold(fragment)){
            fragments.add(fragment);
        }
    }
    
    private boolean hasReachThreshold(Fragment fragment){
        return fragments.stream()
                    .filter(fg -> fg.getMutexFileUUID()
                            .equals(fragment.getMutexFileUUID()))
                    .count() >= 2;
    }
    
    public String getFileName(String uuid){
        return mutexFileDAO.findById(UUID.fromString(uuid))
                .map(MutexFile::getFileName).getOrElse(() -> "");
   }
    
    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Set<Fragment> getFragments() {
        return fragments;
    }

}
