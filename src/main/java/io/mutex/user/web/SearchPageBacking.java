/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.index.entity.Inode;
import io.mutex.search.valueobject.Fragment;
import io.mutex.index.service.IndexNameUtils;
import io.mutex.search.service.ElasticResponseHandler;
import io.mutex.search.service.VirtualPageService;
import io.mutex.index.repository.InodeDAO;
import io.mutex.search.valueobject.CompletionSuggestionFragment;
import io.mutex.search.valueobject.PhraseSuggestionFragment;
import io.mutex.search.valueobject.TermSuggestionFragment;
import io.mutex.search.valueobject.VirtualPage;
import io.mutex.user.entity.Group;
import io.mutex.index.service.FileIOService;
import io.mutex.search.service.TextHandlingService;
import io.mutex.search.service.PreviewService;
import io.mutex.search.service.SuggestService;
import io.mutex.user.service.UserGroupService;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 *
 * @author Florent
 */
@Named(value = "searchPageBacking")
@ViewScoped
public class SearchPageBacking extends QuantumBaseBacking implements Serializable{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(SearchPageBacking.class.getName());
    
    private @Inject FacesContext facesContext;
    private @Inject ExternalContext externalContext;
     
    @Inject VirtualPageService searchVirtualPageService;
    @Inject PreviewService searchPreviewService;
    @Inject IndexNameUtils elasticApiUtils;
    @Inject ElasticResponseHandler responseHandler;
    @Inject InodeDAO inodeDAO;
    @Inject UserGroupService userGroupService;
    @Inject FileIOService fileIOService;
    @Inject TextHandlingService textService;
    @Inject SuggestService suggestService;
    @Inject PreviewService previewService;
    
   
    private List<Group> groups;// = new ArrayList<>();
   

    private List<Group> selectedGroups = new ArrayList<>();
    private List<VirtualPage> previews = new ArrayList<>();
    private Group selectedGroup;
    private Fragment selectedFragment;
    private String searchText;
    private SortedSet<Fragment> fragments = new TreeSet<>();
    private List<TermSuggestionFragment> termSuggestions = new ArrayList<>();
    private List<PhraseSuggestionFragment> phraseSuggestions = new ArrayList<>();
    private List<CompletionSuggestionFragment> completionSuggestions = new ArrayList<>();
        
    @PostConstruct
    public void init(){
       groups = initGroups();
    }
    
    private List<Group> initGroups(){
        return getAuthenticatedUser().map(userGroupService::getGroups)
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    public void search(){
       if(searchText != null){
           fragments = searchVirtualPageService.search(selectedGroups, searchText);
       }
    }
    
    public void suggest(){
        if(searchText != null){
//            termSuggestions = suggestService.suggest(selectedGroups, searchText);
            phraseSuggestions = suggestService.suggestPhrase(selectedGroups, searchText);
        }
    }
     
    public void complete(){
//       completionSuggestions = suggestService.complete(selectedGroups, searchText);
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

	public FacesContext getFacesContext() {
		return facesContext;
	}

	public void setFacesContext(FacesContext facesContext) {
		this.facesContext = facesContext;
	}

	public ExternalContext getExternalContext() {
		return externalContext;
	}

	public void setExternalContext(ExternalContext externalContext) {
		this.externalContext = externalContext;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Group> getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(List<Group> selectedGroups) {
		this.selectedGroups = selectedGroups;
	}

	public List<VirtualPage> getPreviews() {
		return previews;
	}

	public void setPreviews(List<VirtualPage> previews) {
		this.previews = previews;
	}

	public Group getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(Group selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	public Fragment getSelectedFragment() {
		return selectedFragment;
	}

	public void setSelectedFragment(Fragment selectedFragment) {
		this.selectedFragment = selectedFragment;
	}

	public List<TermSuggestionFragment> getTermSuggestions() {
		return termSuggestions;
	}

	public void setTermSuggestions(List<TermSuggestionFragment> termSuggestions) {
		this.termSuggestions = termSuggestions;
	}

	public List<PhraseSuggestionFragment> getPhraseSuggestions() {
		return phraseSuggestions;
	}

	public void setPhraseSuggestions(List<PhraseSuggestionFragment> phraseSuggestions) {
		this.phraseSuggestions = phraseSuggestions;
	}

	public List<CompletionSuggestionFragment> getCompletionSuggestions() {
		return completionSuggestions;
	}

	public void setCompletionSuggestions(List<CompletionSuggestionFragment> completionSuggestions) {
		this.completionSuggestions = completionSuggestions;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setFragments(SortedSet<Fragment> fragments) {
		this.fragments = fragments;
	}
    
    

}
