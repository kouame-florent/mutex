/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import quantum.mutex.domain.VirtualPage;
import quantum.mutex.service.search.SearchService;

/**
 *
 * @author Florent
 */
@Named(value = "searchBacking")
@ViewScoped
public class SearchBacking implements Serializable{
    
    @Inject SearchService searchService;
    
    private String searchText;
    private final Set<VirtualPage> results = new HashSet<>();
    
    public void search(){
        results.clear();
        results.addAll(searchService.search(searchText));
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Set<VirtualPage> getResults() {
        return results;
    }
    
    
}
