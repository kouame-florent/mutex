/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.type.Fragment;
import quantum.mutex.util.QueryUtils;
import quantum.mutex.service.search.ElasticResponseHandler;
import quantum.mutex.service.search.SearchVirtualPageService;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.type.MutexCompletionSuggestion;
import quantum.mutex.domain.type.MutexPhraseSuggestion;
import quantum.mutex.domain.type.MutexTermSuggestion;
import quantum.mutex.domain.type.VirtualPage;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.service.FileIOService;
import quantum.mutex.service.TextHandlingService;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.service.search.PreviewService;
import quantum.mutex.service.search.SuggestService;


/**
 *
 * @author Florent
 */
@Named(value = "searchPageBacking")
@ViewScoped
public class SearchPageBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(SearchPageBacking.class.getName());
    
    private @Inject FacesContext facesContext;
    private @Inject ExternalContext externalContext;
     
    @Inject SearchVirtualPageService searchVirtualPageService;
    @Inject PreviewService searchPreviewService;
    @Inject QueryUtils elasticApiUtils;
    @Inject ElasticResponseHandler responseHandler;
    @Inject InodeDAO inodeDAO;
    @Inject UserGroupService userGroupService;
    @Inject FileIOService fileIOService;
    @Inject TextHandlingService textService;
    @Inject SuggestService suggestService;
    @Inject PreviewService previewService;
    
    @Getter 
    private List<Group> groups;// = new ArrayList<>();
   
    @Getter @Setter
    private List<Group> selectedGroups = new ArrayList<>();
    @Getter @Setter
    private List<VirtualPage> previews = new ArrayList<>();
   
    @Getter @Setter
    private Group selectedGroup;
    @Getter @Setter
    private Fragment selectedFragment;
    private String searchText;
    private Set<Fragment> fragments = new LinkedHashSet<>();
    @Getter
    private List<MutexTermSuggestion> termSuggestions = new ArrayList<>();
    @Getter
    private List<MutexPhraseSuggestion> phraseSuggestions = new ArrayList<>();
    @Getter
    private List<MutexCompletionSuggestion> completionSuggestions = new ArrayList<>();
        
    @PostConstruct
    public void init(){
       groups = initGroups();
    }
    
    private List<Group> initGroups(){
        return getUser().map(userGroupService::getGroups)
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    public void search(){
       fragments = searchVirtualPageService.search(selectedGroups, searchText);
    }
    
    public void suggest(){
        termSuggestions = suggestService.suggestTerm(selectedGroups, searchText);
        phraseSuggestions = suggestService.suggestPhrase(selectedGroups, searchText);
    }
     
    public void complete(){
       completionSuggestions = suggestService.complete(selectedGroups, searchText);
    }

    public void prewiew(Fragment fragment){
        Optional<VirtualPage> rVp =
                previewService.prewiew(fragment, selectedGroups, searchText);
        previews = rVp.map(vp -> List.of(vp))
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    public String getFileName(String uuid){
        return inodeDAO.findById(uuid)
                .map(Inode::getFileName).orElseGet(() -> "");
    }
    
    public String sanitize( String text){
        return textService.sanitize(text);
    }
    
    public void download(Fragment fragment){
        fileIOService.download(facesContext,fragment);
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
