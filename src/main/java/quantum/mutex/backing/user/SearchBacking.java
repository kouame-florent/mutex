/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
import quantum.mutex.service.api.ElasticApiUtils;
import quantum.mutex.service.api.ElasticResponseHandler;
import quantum.mutex.service.api.ElasticSearchService;

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
    @Inject ElasticApiUtils elasticApiUtils;
    @Inject ElasticResponseHandler responseHandler;
    @Inject MutexFileDAO mutexFileDAO;
    
    private String searchText;
    private List<Fragment> fragments;
    
    @PostConstruct
    public void init(){
        fragments = initSearchResult();
    }
    
    private List<Fragment> initSearchResult(){
        return Collections.EMPTY_LIST;
    }
    
    public void search(){
        fragments = getUserPrimaryGroup()
                .flatMap(g -> searchService.search(g, searchText))
                .flatMap(j -> responseHandler.marshall(j))
                .map(jo -> responseHandler.getFragments(jo))
                .getOrElse(() -> Collections.EMPTY_LIST);
       
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


    public List<Fragment> getFragments() {
        return fragments;
    }

}
