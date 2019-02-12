/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.dto.Fragment;
import quantum.mutex.service.PermissionFilterService;
import quantum.mutex.service.api.QueryUtils;
import quantum.mutex.service.api.ElasticResponseHandler;
import quantum.mutex.service.api.SearchService;
import quantum.mutex.util.Constants;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.entity.Group;

/**
 *
 * @author Florent
 */
@Named(value = "searchBacking")
@ViewScoped
public class SearchBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(SearchBacking.class.getName());
     
    @Inject SearchService searchService;
    @Inject PermissionFilterService permissionFilterService;
    @Inject QueryUtils elasticApiUtils;
    @Inject ElasticResponseHandler responseHandler;
    @Inject InodeDAO mutexFileDAO;
    
    @Inject ToolBarBacking toolBarBacking;
   
    private String searchText;
    private Set<Fragment> fragments = new LinkedHashSet<>();
    
    @PostConstruct
    public void init(){
        
    }
    
    public void search(){
      processSearchStack();
    }
   
    private final Function<String,Set<Fragment>> matchQuery = text -> {
       return getUser()
                .flatMap(u -> searchService.searchForMatch(u, text))
                .flatMap(j -> responseHandler.marshall(j))
                .map(jo -> responseHandler.getFragments(jo))
                .getOrElse(() -> Collections.EMPTY_SET);
   };
   
    private final Function<String,Set<Fragment>> matchPhraseQuery = text -> {
       return getUser()
                .flatMap(u -> searchService.searchForMatchPhrase(u, text))
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
                .map(Inode::getFileName).getOrElse(() -> "");
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
