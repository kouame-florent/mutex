/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import quantum.mutex.dto.VirtualPageDTO;
import quantum.mutex.service.PermissionFilterService;
import quantum.mutex.service.api.ElasticSearchService;

/**
 *
 * @author Florent
 */
@Named(value = "searchBacking")
@ViewScoped
public class SearchBacking implements Serializable{
    
    @Inject ElasticSearchService searchService;
    @Inject PermissionFilterService permissionFilterService;
    
    private String searchText;
    private final Set<VirtualPageDTO> results = new HashSet<>();
    
    public void search(){
//        results.clear();
//        results.addAll(permissionFilterService
//                .withPermissions(searchService.search(searchText)));
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
