/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.dto.VirtualPageDTO;
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
    
    private String searchText;
    private final Set<VirtualPageDTO> results = new HashSet<>();
    
    public void search(){
//        results.clear();
//        results.addAll(permissionFilterService
//                .withPermissions(searchService.search(searchText)));

        List<String> contents = getUserPrimaryGroup()
                .flatMap(g -> searchService.search(g, searchText))
                .flatMap(j -> responseHandler.marshall(j))
                .map(jo -> responseHandler.getPages(jo))
                .getOrElse(() -> Collections.EMPTY_LIST);
        
        contents.forEach(c -> LOG.log(Level.INFO, "--> CONTENTS: {0}", c));
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Set<VirtualPageDTO> getResults() {
        return results;
    }
    
    
}
